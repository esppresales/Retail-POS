<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use api\modules\v1\models\TimeTracking;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class TimeTrackingController extends Controller

{

    public function behaviors()

    {

        return [

            'verbs' => [

                'class' => VerbFilter::className(),

                'actions' => [

                    'index'=>['get'],

                    'view'=>['get'],

                    'create'=>['post'],

                    'update'=>['put'],

                    'delete' => ['delete'],

                ],



            ],

        ];

    }

 



    public function beforeAction($event)

    {

        $action = $event->id;

        if (isset($this->actions[$action])) {

            $verbs = $this->actions[$action];

        } elseif (isset($this->actions['*'])) {

            $verbs = $this->actions['*'];

        } else {

            return $event->isValid;

        }

        $verb = Yii::$app->getRequest()->getMethod();



        $allowed = array_map('strtoupper', $verbs);



        if (!in_array($verb, $allowed)) {

            

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,

                'msg' => 'Method not allowed'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

            exit;



        }  



        return true;  

    }

 

    /**

    * Lists all log-in and loout time of all employees.

    * @return mixed

    */  



    public function actionIndex()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

       // Yii::$app->common_functions->checkAccess($access_token);

//        $query = new Query;
//
//        $query->from('time_tracking')
//
//              ->select("distinct(user_id)");
//
//
//
//        $command = $query->createCommand();
//
//        $user_id_array = $command->queryAll();
//
//        
//
//        $query->from('time_tracking')
//
//              ->select("distinct(created_date)")
//        
//              ->orderby("id DESC");
//
//
//
//        $command = $query->createCommand();
//
//        $date_array = $command->queryAll();
//
//        $newModels = array();
//
//                
//
//        foreach ($user_id_array as $key => $users) {
//
//        $i = 0;
//
//            foreach ($date_array as $key => $value) {
//
//
//
//                $query = new Query;
//
//                $query->from('time_tracking')
//
//                      ->join('INNER JOIN','users','time_tracking.user_id=users.id')
//
//                      ->where('time_tracking.created_date="'.$value['created_date'].'"')
//
//                      ->select("user_id");
//
//
//
//                $command = $query->createCommand();
//
//                $user_ids = $command->queryAll();   
//
//                foreach ($user_ids as $user_id) {
//
//                    
//
//                    $query = new Query;
//
//                    $query->from('time_tracking')
//
//                          ->join('INNER JOIN','users','time_tracking.user_id=users.id')
//
//                          ->where('time_tracking.created_date="'.$value['created_date'].'"')
//
//                          ->andWhere('time_tracking.user_id="'.$user_id['user_id'].'"')
//
//                          ->orderby('clock_in_time DESC')
//
////                          ->orderby('time_tracking.user_id')
//
//                          ->select("time_tracking.created_date, users.first_name, users.last_name, time_tracking.user_id, time_tracking.clock_in_time, time_tracking.clock_out_time");
//
//
//
//                    $command = $query->createCommand();
//
//                    $time_array = $command->queryAll();
//
//                    $log_in_time = $time_array[0]['clock_in_time'];
//
////                    $max = -9999999; //will hold max val
//
//                    $found_item = null; //will hold item with max val;
//
//                    $max = 0.00;
//
//                    $duration = 0;
//
//                    foreach($time_array as $k=>$v)
//
//                    {
//
//                        if($v['clock_out_time']>$max)
//
//                        {
//
//                           $max = $v['clock_out_time'];
//
////                           $found_item = $v;
//
//                        }
//
//                          $duration = (strtotime($v['clock_out_time']) - strtotime($v['clock_in_time'])) + $duration;
//
//                    }
//
//                    $duration = gmdate("H:i", $duration).' ';
//
//                    $newModels[$i]['user_id'] = $time_array[0]['user_id'];
//
//                    $newModels[$i]['created_date'] = date('d-M-Y',  strtotime($value['created_date']));
//
//                    $newModels[$i]['user_name'] = ucfirst($time_array[0]['first_name']).' '.ucfirst($time_array[0]['last_name']);
//
//                    $newModels[$i]['log_in_time'] = date('h:i',  strtotime($log_in_time));
//
//                    $newModels[$i]['log_out_time'] = date('h:i', strtotime($max));
//
//                    $newModels[$i]['duration'] = $duration;
//
//                    $i++;
//
//                }                
//
//            }            
//
//        }
//
//        $newModels = array_map("unserialize", array_unique(array_map("serialize", $newModels)));
//
//        foreach ($newModels as $value) {
//
//            $time_report[] = $value;
//
//            
//
//        }
        
        $query = new Query;

        $query->from('time_tracking')
                
              ->join('INNER JOIN','users','time_tracking.user_id=users.id')

              ->select("time_tracking.created_date, time_tracking.clock_in_time, time_tracking.clock_out_time, users.first_name, users.last_name, users.id ")
                
              ->orderby("time_tracking.id DESC");

        $command = $query->createCommand();

        $time_array = $command->queryAll();
        
        $newModels = array();
        $i = 0;
        foreach ($time_array as $value) {
//echo $value['clock_out_time'];exit;
            if($value['clock_out_time'] == '0000-00-00 00:00:00' || $value['clock_out_time'] == '')
            {
                $clock_out_time = '00:00';
                $log_out_time = '00:00';
                // Code added by Divya to resolve issue of time tracking duration field  
                $current_time= gmdate('Y-m-d H:i:s'); // Removed "A" from this time by Mohit Garg for time tracking issue               
                $in_time= date('Y-m-d H:i:s', strtotime($value['clock_in_time']));
                $seconds = strtotime($current_time)- strtotime($in_time);
                $tmin   = abs(round($seconds/ 60));
//                $days    = floor($seconds / 86400);
//                $hours   = floor(($seconds - ($days * 86400)) / 3600);
//                $minutes = floor(($seconds - ($days * 86400) - ($hours * 3600))/60);
//                $seconds = floor(($seconds - ($days * 86400) - ($hours * 3600) - ($minutes*60)));
                $tm= $tmin%60;
                $th=$tmin/60;
                $duration =  sprintf("%02d", $th).':'.sprintf("%02d", $tm);  
                // Code ends here //
            }
            else
            {
                $log_out_time = $value['clock_out_time'];
                $clock_out_time = date('d-M-Y h:i:s A',  strtotime($value['clock_out_time']));
                 // Code added by Divya to resolve issue of time tracking duration field 
                $in_time= date('Y-m-d H:i:s', strtotime($value['clock_in_time']));
                $seconds = strtotime($log_out_time)- strtotime($in_time);
                 $tmin   = abs(round($seconds/ 60));
//                $days    = floor($seconds / 86400);
//                $hours   = floor(($seconds - ($days * 86400)) / 3600);
//                $minutes = floor(($seconds - ($days * 86400) - ($hours * 3600))/60);
//                $seconds = floor(($seconds - ($days * 86400) - ($hours * 3600) - ($minutes*60)));
                $tm= $tmin%60;
                $th=$tmin/60;
                $duration =  sprintf("%02d", $th).':'.sprintf("%02d", $tm);
                // Code ends here //
               // $duration = gmdate("H:i:s", strtotime($log_out_time) - strtotime(date('h:i:s',  strtotime($value['clock_in_time']))));
            }
            $newModels[$i]['user_id'] = $value['id'];
            $newModels[$i]['user_name'] = ucfirst($value['first_name']).' '.ucfirst($value['last_name']);
            $newModels[$i]['created_date'] = date('d-M-Y',  strtotime($value['created_date']));
            $newModels[$i]['log_in_time'] = date('d-M-Y h:i:s A',  strtotime($value['clock_in_time']));
            $newModels[$i]['log_out_time'] = $clock_out_time;
            $newModels[$i]['duration'] = $duration;
//            $newModels[$i]['user_name'] = ucfirst($value['first_name']).' '.ucfirst($value['last_name']);
            $i++;
        }
        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$newModels,

//                        'msg' => 'Customer updated successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

    



}

