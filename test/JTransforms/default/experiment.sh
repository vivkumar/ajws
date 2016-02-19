
#####################################################

if [ $# -lt 1 ]
then
	echo "USAGE: ./experiment.sh <THREADS>"
	exit
fi

###### STANDARD ARGUMENTS #########

nthread=$1
threadsBegin2D=65636
threadsBegin3D=65636
niter=2
doWarmup="false"
doScaling="true"
nsize=1
nsize_1=1048576
nsize_2=1024
nsize_3=128

###################################

BENCHMARK="edu.emory.mathcs.jtransforms.AllTest"

PWD=`pwd`
LIB="$PWD/lib"
cd bin

java -cp .:$LIB/asm-3.0.jar:$LIB/cobertura.jar:$LIB/jakarta-oro-2.0.8.jar:$LIB/junit-4.8.2.jar:$LIB/asm-tree-3.0.jar:$LIB/hamcrest-core-1.2.jar:$LIB/log4j-1.2.9.jar $BENCHMARK $nthread $threadsBegin2D $threadsBegin3D $niter $doWarmup $doScaling $nsize $nsize_1 $nsize_2 $nsize_3

