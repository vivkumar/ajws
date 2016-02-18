JIKESRVM=~/vivek_work/work-stealing/rvm/production_ws/rvm
OUTDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal/results
TOPDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal
####################################################
####################################################
if [ $# -lt 1 ]
then
  echo "USAGE: ./execute  <WS_THREADS"
  exit
fi

name="jmetal.experiments.studies.StandardStudy"

$JIKESRVM -Xms921M -Dout.dir=$OUTDIR -X:gc:threads=$1 -Xws:procs=$1 -Xws:autoThreads=true -cp ./jmetal-ws.jar $name $2
