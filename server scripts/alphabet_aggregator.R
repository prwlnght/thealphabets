#This file is a features_extractor file. 
#Input: csv with features vectors as columns, annotated by the feature name as filename, 
#Output: A single csv with with column ( name, ID, aggregated_feature_like_mean,.....aggregated_features_for_subsection, ...) 
#Set #segmentation length as appropriate to get more features (a signal with frequency of 50 will give five segments)
#copyright @prwl_nght 2016

require(stats)
#remove tempdata
if (!exists("segmentation_length")) segmentation_length = 10 
print(segmentation_length)

args <- commandArgs(TRUE)
mystr <- args[1]
#mystr <- 'gautam.zip'
name <- strsplit(mystr,"\\.")[[1]][1]
print(name)

data_directory <- paste('uploads\\',name,'/',sep = "")
#data_directory <- 'C:\\xampp\\htdocs\\test\\uploads\\prajwal/'
setwd(data_directory)
#this is where to loop 

filenames_all <- list.files(pattern ="csv", full.names=TRUE)
#read a file to get info
test0 <- read.csv(filenames_all[1], header = FALSE)
#features already read
number_of_features_in_file = ncol(test0)
features_in_file <- c("EMGO", "EMG1", "EMG2", "EMG3", "EMG4", "EMG5", "EMG6", "EMG7", "ACCLX","ACCLY", "ACCLZ", "GYR1", "GYR2", "GYR3", "ORN1", "ORN2", "ORN3" )
#number of segmentations depends on the number of datapoints and the number of desired segmentations
segmentations = 5#nrow(test0)/segmentation_length #5

#moments to compute
moments_to_compute <- c('mean', 'max', 'min', 'stdev', 'total_energy')
#moments_to_compute <- c('total_energy')
#the total number of features desired. The plus 2 is for name of signal and the id number 
number_of_feature_columns <<- number_of_features_in_file*length(moments_to_compute) + segmentations*number_of_features_in_file*length(moments_to_compute) + 1
feature_row_names  <- c()
feature_column_names <- c()
feature_column_names[1] <- "Name"
i=0;j=0; 
feature_column_counter <- 1
for (j in 0:segmentations){
  for (i in 1:length(moments_to_compute)){
  for (feature_name_index in 1:length(features_in_file )){
  this_column_name <- paste(features_in_file[feature_name_index], "_", moments_to_compute[i], "_", j, sep="")
  feature_column_counter <- feature_column_counter + 1
  feature_column_names[feature_column_counter] <- this_column_name
  
  
  }
}
}
#initialize the dataframe 
features_extracted <<- data.frame(matrix(vector(), length(filenames_all),number_of_feature_columns))

# this_colnames <- c(paste('means', index, sep="_"), paste('maxs', index, sep="_"), paste('mins', index, sep="_"), paste('stdevs', index, sep="_"),paste('total_energy', index, sep="_"))
# new_colnames <- c(previous_colnames, this_colnames)
# colnames(features_extracted) <<- column_names

#debug marker

compute_moments <- function(this_block, index){
  #features
  this_means <- colMeans(this_block)
  this_maxs <-  apply(this_block, 2, max)
  this_mins <- apply(this_block, 2, min)
  this_stdevs <- apply(this_block, 2, sd)
  this_total_energy <- colSums(this_block^2)
  
  all_features <- c(this_means, this_maxs, this_mins, this_stdevs, this_total_energy)
  #fixing colnames
  return(all_features)
}

for (file_index in 1: length(filenames_all)){
  
test1 <- read.csv(filenames_all[file_index], header = FALSE)

#set column names #specific to single hand alphabet recognition 
#column_names <- c('EMG0', 'EMG1',  'EMG2',  'EMG3',  'EMG4',  'EMG5',  'EMG6',  'EMG7',  'ACCLX', 'ACCLY',  'ACCLZ',  'GYR_A', 'GYR_B', 'GYR_C', 'ORN_A', 'ORN_B', 'ORN_C')

#read overall corpus moments (mean, max, min)

#featurecolnames(test1) <- column_names


this_file <- filenames_all[file_index]
split_this <- strsplit(this_file, "_")[[1]]
this_file_name <- paste(strsplit(split_this, "/")[[1]][2], "_", split_this[2], sep="")
this_file_id <- strsplit(split_this[3],"\\.")[[1]][1]

#the loop will build a vector that it will place in the file_index^th row

this_sign_moments <- c()
this_sign_moments[1] <- this_file_name
#populate the first item as the name of the signal and the second as a combination of name and id for easier access later
feature_row_names[file_index] <- paste(this_file_name, this_file_id, sep="_")
#this computes the overall moments
this_sign_moments <- c(this_sign_moments, compute_moments (test1, 0))
#insert this into 

#this computes the moments for the individual segments. 
for (i in 1:segmentations){
  start_index <- ((i-1)*segmentation_length+1)
  stop_index <- i*segmentation_length
  segmented_block <- test1[start_index:stop_index,]
  this_sign_moments <- c(this_sign_moments, compute_moments(segmented_block, i))
  #insert into 
  
}

#write to the dataframe
features_extracted[file_index,] <- this_sign_moments
#remove the temp variable
remove(this_sign_moments)
#write features file with gesture name and identity of gesture. 


}
rownames(features_extracted) <- feature_row_names
colnames(features_extracted) <- feature_column_names

#create features folder if it does not exists
if (! dir.exists('features/')){
  
  dir.create('features/')

}

#write all features to one file 
outputfilename <- paste('features','.csv', sep = "")
outputfilepath <- paste(getwd(),"\\features", '/', outputfilename, sep="")
write.csv(features_extracted, file= outputfilepath)
rm(list = ls())

#Alphabet.R starts
##################################################################################################################################################################
#DECS
#normalization on/off
normalization_which <- c(0, 0, 0, 0)
corpus <- LETTERS 
number_of_trainig_instances <- 130 #TODO get this dynamically 
##################################################################################################################################################################

#FUNCTIONS
##################################################################################################################################################################
normalize <- function(column_indices_to_normalize){
  
  for (column_in_dex in column_indices_to_normalize)  {
    this_max <- max(signed_features[column_in_dex] )
    this_min <- min(signed_features[column_in_dex] )
    signed_features[column_in_dex] <<- sapply(signed_features[column_in_dex], function (x) (x-this_min)/(this_max-this_min))
  }
}
store_features <- data.frame(matrix(nrow=510, ncol=7))

feature_selection <- function(dff){
  main_counter <- 0
  
  for (this_alphabet_number in 1:length(corpus)){
    this_alphabet <- corpus[this_alphabet_number]
    #for rownames that contain alphabet_this_alphabet assign to training corpus
    print(this_alphabet)
    to_test <- paste('alphabets_', this_alphabet, sep="" )
    #train_df <- dff[which(sapply(rownames(dff), function(x) any(grepl(to_test, x))))]
    #test_df <- dff[-(sapply(rownames(dff), function(x) any(grepl(to_test, x))))]
    
    #for f in feature
    for(feature_name_index in 1:length(colnames(dff))){
      main_counter <- main_counter + 1
      
      sorted_list <- dff[order(dff[feature_name_index]),]
      this_dff <- sorted_list[feature_name_index]
      #calculates the relative range / number of iterations over 133 (worst possible)
      this_range <- range(which(sapply(rownames(sorted_list), function(x) any(grepl(to_test, x)))))
      if (this_range[1] != 1) threshold_lower <- (this_dff[(this_range[1]-1),] + this_dff[this_range[1],] ) / 2 
      else threshold_lower <- this_dff[this_range[1],]
      if (this_range[2] != number_of_trainig_instances) threshold_upper <- (this_dff[this_range[2]+1,] + this_dff[(this_range[2]),]) / 2 
      else threshold_upper <- this_dff[this_range[2],]
      thresholds <- c(threshold_lower, threshold_upper)
      #get 5 and 133 dynamically TODO 
      if (diff(this_range) ==4 ) weight <- 3
      else weight <- abs(log((diff(this_range) - 4)/(133-4), base=10))
      store_features [main_counter,] <<-   c(to_test,  colnames(dff)[feature_name_index],  this_range, thresholds, weight)
      
    }
    
  }
}

logistic_regression <- function(){
  
  
}

naive_bayes <- function (){
  
  
}

piecewise_energy <- function (){
  #make a dataframe with to  EMG energies 
  no_energy_filer_emg <- which(!sapply(colnames(signed_features), function(x) any(grepl('EMG.*energy.', x))))
  #energy_filer_emg_energy <- which(sapply(energy_filer , function(x) any(grepl('EMG', x))))
  energy_dataframe <- signed_features[-no_energy_filer_emg]
  
  
  for(accessindex in 1:nrow(energy_dataframe)){
    difference_frame <- energy_dataframe[-accessindex,] - as.numeric(as.vector(energy_dataframe[accessindex,]))
    emg_total_0 <- difference_frame[1] + difference_frame[2] + difference_frame[3] + difference_frame[4] + difference_frame[5] + difference_frame[6] + difference_frame[7] 
    
    print('a')
  }
}
#
# computeAccuracy_total<- function(){
#   #read the file that has better data
#   energy_features <<- read.csv("features/Energy_only.csv")
#   
#   for(energy_features_index in 1:nrow(energy_features)){
#     train_data <- energy_features[energy_features_index,2:9]
#     test_data <- energy_features[-energy_features_index,2:9]
#     difference_energy <- sweep(test_data, 2, as.numeric(as.vector(train_data), "-"))
#     rownames(difference_energy) <- energy_features[,1][-energy_features_index]
#     sorted_difference_energy <- sort(abs(rowSums(difference_energy)))
#     
#     #if sorted_difference_energy_names[1] == train_data.name then correct = correct + 1
#     
#   }
#  energy_features_index <- 0
#}

character_wise_energy_analysis <- function(){
  #This function takes the total energy arrays (for entire pods) and prints average energy per pod per letter 
  
  #for instance 
  for (row_traverse_index in 1:nrow(signed_features)){
    pod_energy_a <<- vector(mode="numeric", length=8)
    pod_energy_b <<- vector(mode="numeric", length=8)
    this_rowname <- as.character(rownames(signed_features)[row_traverse_index])
    if(grepl("_a", this_rowname)){
      for (i in 1:8){
        #print(i)
        pod_energy_a[i] <<- pod_energy_a[i] + signed_features[1,][i]
      }
    }
    if(grepl("_b", this_rowname)){
      for (i in 1:8){
        #print(i)
        pod_energy_b[i] <<- pod_energy_b[i] + signed_features[1,][i]
      }
      
    }
    print(pod_energy_a)
    print(pod_energy_b)
    #rm(pod_energy_a, pod_energy_b) 
    
  }
  
}
##################################################################################################################################################################
#PROCEDURE
##################################################################################################################################################################
##################################################################################################################################################################
##################################################################################################################################################################
#read the file

signed_features <<- read.csv("features/features.csv")#read.csv(paste("features/",name,"_features.csv",sep = ""))
feature_bases <- c('mean', 'max', 'min', 'stdev', 'total_energy')

#get id as the rownames
rownames(signed_features) <- signed_features[,1]

signed_features <- signed_features[-c(1,2)]

#normalize the features as dictated by normalize_which
#TODO get a list of features and optimize to call normalize with a definitive list 
if(normalization_which[1]) normalize(which(sapply(colnames(signed_features), function(x) any(grepl('EMG', x)))))
if(normalization_which[2]) normalize(which(sapply(colnames(signed_features), function(x) any(grepl('ACCL', x)))))
if(normalization_which[3]) normalize(which(sapply(colnames(signed_features), function(x) any(grepl('GYR', x)))))
if(normalization_which[4]) normalize(which(sapply(colnames(signed_features), function(x) any(grepl('ORN', x)))))


#this should result in a df? features, thresholds, weights for each alphabet

#obtain a list of features 


feature_selection(signed_features[1:85])
colnames(store_features) <- c("alphabet_name", "Features", "Range_Lower", "Range_Upper", "Threshold_lower", "Threshold_upper", "Weight")
temp_max <- as.numeric(max(store_features$Weight ))
temp_min <- as.numeric(min(store_features$Weight ))

normalized_weight <- ( as.numeric(as.vector(store_features$Weight)) - temp_min)/(temp_max - temp_min)
store_features <- cbind(store_features, normalized_weight)
outputfilepath_feature_selection  <- paste('features/','feature_selection_working.csv',sep ="")
write.csv(store_features, file= outputfilepath_feature_selection) 

outputfilepath_feature_normalized  <- paste('features/','feature_normalized.csv',sep ="")#'features/feature_normalized.csv'
write.csv(signed_features, file= outputfilepath_feature_normalized) 






#now 


