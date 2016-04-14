#install.packages("RTextTools")
library(RTextTools)
setwd("//Users//shivugururaj//Documents//gdrive//spring 2016//machine learning//assignments//assignment4_sxg144730")
r<-read_data("SampleData",type = "folder",index = "labels.csv", warn=F)
doc_matrix <- create_matrix(r$Text.Data, language="english",removeNumbers=TRUE)
container <- create_container(doc_matrix, r$Labels, trainSize=1:240,testSize=241:280, virgin=FALSE)

TREE <- train_model(container,"TREE")
TREE_CLASSIFY <- classify_model(container, TREE)

GLMNET <- train_model(container,"GLMNET")
GLMNET_CLASSIFY <- classify_model(container, GLMNET)

SVM <- train_model(container,"SVM")
SVM_CLASSIFY <- classify_model(container, SVM)

MAXENT <- train_model(container,"MAXENT")
MAXENT_CLASSIFY <- classify_model(container, MAXENT)

BOOSTING <- train_model(container,"BOOSTING")
BOOSTING_CLASSIFY <- classify_model(container, BOOSTING)

analytics <- create_analytics(container, cbind(TREE_CLASSIFY, GLMNET_CLASSIFY, SVM_CLASSIFY, MAXENT_CLASSIFY, BOOSTING_CLASSIFY))
summary(analytics)

cat("Tree Accuracy")
cross_validate(container,4,"TREE")
cat("________________________________________________________________")

cat("GLMNET Accuracy")
cross_validate(container,4,"GLMNET")
cat("________________________________________________________________")
cat("SVM Accuracy")
cross_validate(container,4,"SVM")
cat("________________________________________________________________")

cat("MAXENT accuracy")
cross_validate(container,4,"MAXENT")
cat("________________________________________________________________")

cat("BOOSTING accuracy")
cross_validate(container,4,"BOOSTING")
cat("________________________________________________________________")
