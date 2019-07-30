<?php
namespace app\components;
use yii\base\Component;

class CommonFunctions extends Components {
    
    public function status($status) {
        $codes = Array(
        'OK' => 200  ,
        'success_code' => 200,
        'error_code' => 404 ,
        'not_found' => 404
        );
        return (isset($codes[$status])) ? $codes[$status] : '';
    }
        /*
         * Function to calculate age by date of birth
         * @author: Divya Arora
         */
        public function calculateAge($dob){
            # object oriented
        @date_default_timezone_set('America/Los_Angeles');
        @$from = new DateTime($dob);
        @$to   = new DateTime('today');
        echo $from->diff($to)->y;
        }
        
         /*
         * Function to calculate date range
        
         */
         public function getDate($start, $end)
        {
                     
        $dates[$start] = $start; //echo "<pre>"; print_r($dates);exit;
        
        while(end($dates) < $end){
           
        $date_value= @date('Y-m-d', strtotime(end($dates).' +1 day'));
        $dates[$date_value] = @$date_value;
         }
         
       //  echo '<pre>'; print_r($dates);exit;
         return $dates;
        }
        
        //ro check is role is admin or not
            public function isAdmin(){
            
			if(Yii::app()->user->getState('role') == 'admin') {                             
				return true;
			}
			return false;
			
                 }
}

// END OF CLASS
?>