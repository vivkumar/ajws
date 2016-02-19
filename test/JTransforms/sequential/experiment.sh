
###### STANDARD ARGUMENTS #########

nthread=1
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

java -cp jtransforms-seq.jar $BENCHMARK $nthread $threadsBegin2D $threadsBegin3D $niter $doWarmup $doScaling $nsize $nsize_1 $nsize_2 $nsize_3

