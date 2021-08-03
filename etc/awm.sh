# Some useful ACE utils

unset awmConf
declare -A awmConf
awmConf[getconf]="/usr/bin/getconf"
awmConf[ls]="/bin/ls"
awmConf[sleep]="/bin/sleep"
awmConf[kill]="/bin/kill"
awmConf[rm]="/bin/rm -f"
awmConf[date]="/usr/bin/date"
awmConf[ps]="/usr/bin/ps"
awmConf[time]="/usr/bin/time"
awmConf[hostname]="/usr/bin/hostname -s"
awmConf[clkTics]=$(${awmConf[getconf]} CLK_TCK)
awmConf[pid]="$$"
awmConf[statsJSON]=".awm.stats.json"
awmConf[timeStats]=".awm.time.txt"
awmConf[host]=$(${awmConf[hostname]})
awmConf[user]=$USER
awmConf[umask]=$(umask -p)
awmConf[hwmf]="/tmp/awm.$$.hwm"
awmConf[statPID]=0
awmConf[flowPID]=0
awmConf[flowExit]=1

function awmRunCmd {
    local fmt="elapsed:%e kernel:%s user:%U memKB:%M"
    ${awmConf[time]} -o ${awmConf[timeStats]} -f "$fmt" "$@"
    local status=$?
    echo "awmRunCmd: status=${status}"
    cat ${awmConf[timeStats]}
    #${awmConf[rm]} ${awmConf[timeStats]}
}

function awmGetHWM {
    local hwm=0
    if [ ! -z ${awmConf[hwmf]} ] && [ -r ${awmConf[hwmf]} ]; then
        read -r hwm < ${awmConf[hwmf]}
    fi
    echo $hwm
}

function awmSetHWM {
    if [ ! -z ${awmConf[hwmf]} ]; then
        echo $1 > ${awmConf[hwmf]}
    fi
}

function awmTimestamp {
    echo "$(${awmConf[date]} +%k:%M:%S-%a-%d-%b-%Y-%Z)" 
}

function awmMessage {
    local m=$1
    echo "$(awmTimestamp): $m"
}

function awmNowUTC {
    echo "$(${awmConf[date]} -u)"   
}

awmConf[started]=$(awmNowUTC)

# Parameter is varname (not value)
function awmMapToJson {
    local raw=$(declare -p "$1")
    unset _map; local _map
    eval "declare -A _map="${raw#*=}
    local json=""
    local comma=""
    for k in ${!_map[*]}; do
        local v=${_map[$k]}
        #use [[ ... ]] since regexp
        if [[ ! $v =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
            v='"'$v'"'
        fi
        json+=$comma'"'$k'"'':'$v
        comma=','
    done
    echo '{'$json'}'
}

function awmCpuTime {
    local pid=${awmConf[pid]}
    local tl=0
    local statf=/proc/$pid/stat
    if [ -r $statf ]; then
        read -r -a times < $statf
        # utime + stime + cutime + cstime (i.e., this process + children)
        tl=$(( ${times[13]} + ${times[14]} + ${times[15]} +${times[16]} ))
    fi
    echo $tl
}

# Return pids and children (depth first: i.e., leaf then parents... to top)
# We cannot do recursion, since infinite loop as ps is a child...
function awmGetChildPids {
    local pid0=$1
    local pidNppid
    read -r -a pidNppid <<< $( ps -ax -o pid,ppid --no-headers )
    local pidByPpid
    local n=${#pidNppid[@]}
    local i=0
    local pid; local ppid
    while [ $i -lt $n ]; do
        pid=${pidNppid[@]:${i}:1}
        i=$(( $i + 1 ))
        ppid=${pidNppid[@]:${i}:1}
        i=$(( $i + 1 ))
        pidByPpid[$ppid]+=" $pid"
    done
    unset pidNppid
    local cpids=$pid0
    local ppids
    read -r -a ppids <<< ${pidByPpid[$pid0]}
    while [ 0 -lt ${#ppids[@]} ]; do
        local xpids
        unset xpids
        for pid in ${ppids[@]}; do
            cpids="${pid} ${cpids}"
            xpids+=" ${pidByPpid[$pid]}"
        done
        unset ppids
        read -r -a ppids <<< $xpids
    done
    echo $cpids
}

function awmGetMB {
    local val=$1
    local n=${#val}
    local m=`expr $n - 2`
    local nn=${val:0:$m}
    local unit=${val:$m}
    local mb=0
    if [ "$unit" == "kB" ]; then
        mb=$(( $nn * $((1<<10)) / $((1<<20)) ))
    else
        awmMessage "${FUNCNAME[0]}: unexpected unit: $unit"
    fi
    mb=$(( $mb + 1)); #a little extra, in case 0
    echo $mb
}

# Get VmPeak
function awmVmPeakMB {
    local pid=${1:-${awmConf[pid]}}
    local statf=/proc/$pid/status
    local mb=0
    if [ -r $statf ]; then
        while read -r f1 f2 f3; do
            if [ "$f1" == "VmPeak:" ]; then
                mb=$(awmGetMB $f2$f3)
            fi
        done < $statf
    fi
    echo $mb
}

# Get sum of VmPeak of process and children, ...
function awmVmPeakMBTree {
    local pid0=${1:-${awmConf[pid]}}
    local mb=0
    for pid in $(awmGetChildPids $pid0 ) ; do
        local xmb=$(awmVmPeakMB $pid )
        mb=$(( $mb + $xmb ))
    done
    echo $mb
}

function awmDumpStats {
    unset stats; local stats
    declare -A stats
    stats[now]=$(awmNowUTC)
    stats[cpuTime]=$(awmCpuTime)
    stats[vmPeak]=$(awmVmPeakMBTree)
    local hwmMB=$(awmGetHWM)
    if [ ${hwmMB} -lt ${stats[vmPeak]} ]; then
        hwmMB=${stats[vmPeak]}
    else
        stats[vmPeak]=${hwmMB}
    fi
    awmSetHWM ${hwmMB}
    for k in started clkTics host pid user ; do
        stats[$k]=${awmConf[$k]}
    done
    awmMapToJson stats > ${awmConf[statsJSON]}
}

function awmDumpStatsLoop {
    while true; do
        awmDumpStats
        ${awmConf[sleep]} ${awmConf[statT]}
    done
}

# Run this function right after 'flow &' and run as:
# 'awmStartDumpStatsAndWait $!'
function awmStartDumpStatsAndWait {
    awmStartDumpStats ${1} ${2:-30}
    #awmStartDumpStats ${1} ${2:-2}
    wait ${awmConf[flowPID]}
    awmConf[flowExit]=$?
}

function awmExit {
    awmDumpStats  #one before quit
    if [ ${awmConf[statPID]} -gt 0 ]; then
        awmKillPID ${awmConf[statPID]}
    fi
    exit ${awmConf[flowExit]}
}

function awmStartDumpStats {
    awmConf[flowPID]=${1}
    awmConf[statT]=${2:-30}
    ${awmConf[rm]} ${awmConf[statsJSON]}
    awmDumpStatsLoop &
    awmConf[statPID]=$!
}

function awmStopDumpStats {
    if [ 0 -lt ${awmConf[statPID]} ]; then
        awmKillPID ${awmConf[statPID]}
    fi
}

function awmKillPID {
    local pid=$1
    local sig=${2:-TERM}
    ${awmConf[kill]} -s $sig $pid 2>/dev/null
    if [ 0 != $? ]; then
        if [ -d /proc/$pid ]; then
            awmMessage "${FUNCNAME[0]}: warn: Could not kill -s ${sig} ${pid}: $(${awmConf[ps]} -p $pid -o comm --no-headers)"
        fi
    fi
}

# Recursively kill children and then parent.
function awmKillAll {
    local pid=$1
    local sig=${2:-TERM}
    #local cpids=$(${awmConf[ps]} --ppid $pid -o pid --no-headers)
    local cpids=$(awmGetChildPids $pid )
    local cpid
    for cpid in $cpids ; do
        awmKillPID $cpid $sig
    done
}

function awmCleanup {
    local doKill=${1:-false}
    local sig=${2:-TERM}    
    if [ ! -e ${awmConf[statsJSON]} ]; then 
        awmDumpStats 
    fi
    if [ $doKill ]; then 
        # quelch messaging of further signals
        trap awmDoNothing EXIT SIGINT TERM
        awmKillAll ${awmConf[pid]} $sig 
    fi
    if [ -f ${awmConf[hwmf]} ]; then 
        ${awmConf[rm]} ${awmConf[hwmf]} 
        awmConf[hwmf]=""
    fi
}

function awmDoNothing {
    return #need one statement
}

# Even on normal exit, want to be sure to cleanup the awmDumpStatsLoop (and all children too)
trap 'awmCleanup true'   EXIT
trap 'awmCleanup true'   SIGINT TERM
