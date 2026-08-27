McxUtilityApplication :
------------------------

Steps to use the utility

1. Provide the input.txt file with all your input data, input file accepts the input data format like <GroupId> <Action> <Fan names> check sample input provided below.

Mandatory validations to note while writing the input data,

	a. All the parameters should be separated by comma
	b. Action always should be 1 or 2, action 1 for addition and 2 for remove
	c. Make sure that file ends properly with out any empty line

2.input file name should always be as input.txt and should be placed in the location where the jar is located.
3.Utility jar can be found inside XDM lib folder i.e. /DG/activeRelease/xdm/lib/McxUtilityApplication.jar
4.connect to VM and go inside the XDM container and create the custom folder in side the container
5.Copy the jar into created folder inside the XDM container from /DG/activeRelease/xdm/lib/McxUtilityApplication.jar
6.Run . /etc/kodiakDG.conf command
7 Run the java -jar McxUtilityApplication.jar >>output.txt
8.It will start validating the provided input data and once all the validations successful then it will start updating the data in group sharing schema.
9. Output logs will be printed to output.txt file, where you can observe the success or failure responses of the utility.

Sample Syntax for input file:
---------------------------

<GroupId> <action> <externalFanIds with comma seperated>


Sample input file:
------------------

5933,1,1,08081403
5933,2,1,08081403

