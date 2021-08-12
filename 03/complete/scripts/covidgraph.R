require(lattice);

function(param){

#Read parameters
deptId<-param$departmentId
deptName<-param$departmentName
csvFilename<-param$csvFilePath

print(deptId)
print(deptName)
print(csvFilename)


svg();
##********************************************************************************************************************************************

# You can download the file with this instructions
# frsdatafile=download.file(url="https://www.data.gouv.fr/fr/datasets/r/6fadff46-9efd-4c53-942a-54aca783c30c", destfile="/tmp/covid-data.csv")
# frdata <- read.table(file="/tmp/covid-data.csv" , sep=";", h=TRUE);
#**********************************************************************************************************************************************


#Make sure you called the /download endpoint at least once before requesting any visualization
#Load file downloaded from $SERVER:PORT/covid-19/fr/download URI

frdata <- read.table(file=paste(csvFilename) , sep=";", h=TRUE);
names(frdata)
covid_ds = subset(frdata, frdata$dep == deptId)
attach(covid_ds)
#X contient les dates
x<-as.Date(jour,format = "%Y-%m-%d")

# y le nombre  d'hospitalisations
y<-incid_hosp

# z le nombre de réanimation
z<-incid_rea
# Courbe des hospitalisations 

g1<-xyplot(y~x,type="l", ylab="Nouvelles Hospitalisations COVID-19",col="blue",main=paste(" D ",deptId, ":",deptName )); 

print(g1)
grDevices:::svg.off()
}


