#install.packages("e1071")
#install.packages("class")
#install.packages("neuralnet")
#install.packages("rpart")
#install.packages("nnet")


library(e1071)
library(class)
library(neuralnet)
library(rpart)
library(nnet)

#above libraries are used to implement the classifiers and below are the data sets which are tested

dataSet1 <- "http://archive.ics.uci.edu/ml/machine-learning-databases/ionosphere/ionosphere.data"
dataSet2 <- "http://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data"
dataSet3 <- "http://archive.ics.uci.edu/ml/machine-learning-databases/00194/sensor_readings_2.data"
dataSet4 <- "http://archive.ics.uci.edu/ml/machine-learning-databases/soybean/soybean-small.data"
dataSet5 <- "http://archive.ics.uci.edu/ml/machine-learning-databases/postoperative-patient-data/post-operative.data"

dataSetArray <- c(dataSet1,dataSet2,dataSet3,dataSet4,dataSet5)


for(dataSet in dataSetArray){
  for(j in 1:2){
    cat(" ------- Testing = ", j," -------", "\n")
    d <- read.csv(dataSet, header = FALSE, sep = ",")
    samples<-sample(1:nrow(d),size = 0.9*nrow(d))
    trainingData<-d[samples,]
    testData<-d[-samples,]
    
    #last column defines the class. hence considers it for splitting
    colname <- colnames(d)[ncol(d)]
    colnumber <- ncol(d)
    myExpression <-  as.formula(paste(colname,"~.",sep=""))
    testClass <- testData[,colnumber]
    PercepSum <- 0
    
    
    # Decision Tree Classifier
    dt <- rpart(myExpression, data = trainingData, minbucket =5, method="class")
    preddt <- predict(dt,testData, type="class")
    accudt <- (mean(preddt == testData[ , colnumber])) * 100
    method="Decision Tree"
    cat("Classifier = ", method,", Accuracy = ", accudt,"\n")
    
    #Perceptron classifier
    method <-"Perceptron"
    perceptronAccuracy <- 0
    lrmodel <- glm(myExpression, data = trainingData, family = "binomial") 
    lRPredict<-predict(lrmodel,testData, type="response")
    threshold = 0.85
    lRPrediction<-sapply(lRPredict, FUN=function(x) if (x>threshold) 1 else 0)
    perceptron <- table(lRPrediction, testClass)
    perceptronAccuracy <- sum(diag(perceptron)/ sum(perceptron)) *100
    cat("Classifier = ", method,", Accuracy = ", perceptronAccuracy,"\n")
    
    # Neural Network Classifier
    neural <- nnet(myExpression, trainingData,size=1,maxit=4000,decay=0.001,trace = FALSE)
    predn <- predict(neural,testData,type="class")
    nnAccuracy <- mean(predn == testData[ ,colnumber])*100
    method="Neural Network"
    cat("Classifier = ", method,", Accuracy = ", nnAccuracy,"\n")
    
    # Support Vector Machine classifier
    svm <- svm(myExpression, kernel="linear", data = trainingData)
    predsvm <- predict(svm,testData, type="class")
    svmAccuracy <- (mean(predsvm == testData[ , colnumber])) * 100
    method="Support Vector Machine"
    cat("Classifier = ", method,", Accuracy = ", svmAccuracy,"\n")
    
    # Naive Bayesian Classifier
    naivebayes <- naiveBayes(myExpression, data = trainingData)
    prednb <- predict(naivebayes, testData, type = "class")
    naiverBayAccuracy <- (mean(prednb == testData[ , colnumber])) * 100
    method="Naive Bayesian"
    cat("Classifier = ", method,", Accuracy = ", naiverBayAccuracy,"\n")
    
  }
}