<?php
  
    $file_path =  "uploads/";
    $file_name =  $_FILES['uploaded_file']['name'];
	#$file_name = "gautam.zip";
    $file_path = $file_path . basename( $_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        echo "success";
    } else{
        echo "fail";
    }
    #system("./AppFinder.o");
	exec("C:/Users/Gautam/AppData/Local/Programs/Python/Python35-32/python extracter.py $file_name", $output, $return); //C:\Users\Gautam\AppData\Local\Programs\Python\Python35-32
    
    if($return !== 0){

        echo "python exec failed";
            }
   else{
   echo "<br />";
   echo "successfully executed!";   
   }
   exec("Rscript alphabet_aggregator.R $file_name");
 ?>
