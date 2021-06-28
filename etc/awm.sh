declare -A awmConf
awmConf[getconf]="/usr/bin/getconf"
awmConf[ls]="/bin/ls"
awmConf[date]="/usr/bin/date"
awmConf[ps]="/usr/bin/ps"
awmConf[hostname]="/usr/bin/hostname -s"
awmConf[clkTics]=$(${awmConf[getconf]} CLK_TCK)
awmConf[pid]="$$"
awmConf[statsJSON]="/var/spool/awm/$$.json"
awmConf[host]=$(${awmConf[hostname]})
awmConf[user]=$USER
awmConf[jobid]=${AWM_JOBID:-0}
awmConf[nodeDumpT]=${AWM_NODE_DUMP_T:-5}

function awmNowUTC() {
	echo "$(${awmConf[date]} -u)"	
}

awmConf[started]=$(awmNowUTC)

# Parameter is varname (not value)
function awmMapToJson() {
	local raw=$(declare -p "$1")
	eval "declare -A _map="${raw#*=}
	local json=""
	local comma=""
	for k in ${!_map[*]}; do
		local v=${_map[$k]}
		if [[ ! $v =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
			v='"'$v'"'
		fi
		json+=$comma'"'$k'"'':'$v
		comma=','
	done
	echo '{'$json'}'
}

function awmCpuTime() {
	local pid=${awmConf[pid]}
	read -r -a times < /proc/$pid/stat
	# utime + stime + cutime + cstime (i.e., this process + children)
	local tl=$(( ${times[13]} + ${times[14]} + ${times[15]} +${times[16]} ))
	echo $tl
}

function awmProcStatus() {
	local pid=${awmConf[pid]}
	while read -r f1 f2 f3; do
		if [[ "$f1" == "VmPeak:" ]]; then
			echo "$f2 $f3"
		fi
	done < /proc/$pid/status
}

function awmDumpStats() {
	declare -A stats
	stats[now]=$(awmNowUTC)
	stats[cpuTime]=$(awmCpuTime)
	stats[vmPeak]=$(awmProcStatus)
	for k in started clkTics host pid user jobid; do
		stats[$k]=${awmConf[$k]}
	done
	awmMapToJson stats > ${awmConf[statsJSON]}
}

function awmDumpStatsLoop() {
	while true; do
		awmDumpStats
		sleep ${awmConf[nodeDumpT]}
	done
}

function awmCleanup() {
	# Since dumpStats is subshell (different process), we need to find out the child pids and kill
	local cpids=$(${awmConf[ps]} --ppid ${awmConf[pid]} -o pid --no-headers)
	for cpid in $cpids ; do
		kill -s TERM $cpid 2>/dev/null
	done
}

trap 'awmCleanup' SIGINT TERM EXIT
