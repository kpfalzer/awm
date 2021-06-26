declare -A awmConf
awmConf[getconf]="/usr/bin/getconf"
awmConf[ls]="/bin/ls"

# Parameter is varname (not value)
function awmMapToJson {
	local raw=$(declare -p "$1")
	eval "declare -A _map="${raw#*=}
	local json=""
	local comma=""
	for k in ${!_map[*]}; do
		json+=$comma'"'$k'"'':''"'${_map[$k]}'"'
		comma=','
	done
	echo $json
}

function awmCpuTimeMs {
	local pid="$$"
	local clkTics=$( ${awmConf[getconf]} CLK_TCK)
	read -r -a times < /proc/$pid/stat
	# utime + stime + cutime + cstime (i.e., this process + children)
	local tl=$(( ${times[13]} + ${times[14]} + ${times[15]} +${times[16]} ))
	local tmMs=$(( 1000 * $tl / $clkTics ))
	echo $tmMs
}

function awmStarted {
	local pid="$$"
	read -r -a ts <<< $( ${awmConf[ls]} -ld /proc/$pid)
	echo "${ts[7]}${ts[5]}${ts[6]}"
}

function awmProcStatus {
	local pid="$$"
	while read -r f1 f2 f3; do
		if [[ $f1 == "VmPeak:" ]]; then
			echo "$f2 $f3"
		fi
	done < /proc/$pid/status
}
