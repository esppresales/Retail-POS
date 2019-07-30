<?php

/* File Description: Handles Login for a User.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use yii\rest\ActiveController; 

use Yii;

use api\components\CommonFunctions; 

use api\modules\v1\models\Users;

use api\modules\v1\models\LoginForm;

use api\modules\v1\models\TimeTracking;

use yii\data\ActiveDataProvider;

use yii\web\Controller;

use yii\web\NotFoundHttpException;

use yii\filters\VerbFilter;



class LoginController extends Controller

{

    public $model = 'api\modules\v1\models\Users';

    

    public function behaviors()

    {

    return [

        'verbs' => [

            'class' => VerbFilter::className(),

            'actions' => [

                'signin'=>['post'],

            ],

 

        ],];

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

//                  'data' =>array_filter($model_user->attributes),

                'msg' => 'Method not allowed'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//            echo $this->setHeader(400);

//            echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Method not allowed'),JSON_PRETTY_PRINT);

            exit;



        }  



          return true;  

    }



    /*** Function to login the user in the system ***/

    

    public function actionSignin() {

        $model = new LoginForm;

        $model_user = new Users;

        $model_time = new TimeTracking;

        $model->username=$_REQUEST['username'];

        $model->password=$_REQUEST['password'];

        $model_user = $model_user->findByUsername($model->username);

        if(!empty($model_user))

        {

            $is_active = $model_user['is_active'];

            if($is_active)

            {

                $user_id = $model_user->attributes['id'];

                if ( $model->login()) {

                    $timezone = @date_default_timezone_get();

                    $time = @gmdate("Y-m-d H:i:s"); // Removed "a" from gmadate function by Mohit Garg for time tracking issue

                    //$time = date("h:i:sa");

                    $date = @gmdate("Y-m-d");
            
                    // Updating clock out time record for all previous logged in records of this user - MOHIT GARG 8th Jan 2016
                    $out_time = @gmdate("Y-m-d H:i:s"); // Removed "a" from gmadate function by Mohit Garg for time tracking issue
                    $model_time2 = TimeTracking::find()
                                  ->where(['user_id' => $user_id])
                                  ->andWhere(['clock_out_time' => null])
                                  ->all();
                    TimeTracking::updateAll(['clock_out_time' => $out_time], ['and',['=', 'user_id', $user_id],['<=', 'clock_out_time', null]]);
                    if(!empty($model_time2))
                    {
                      foreach ($model_time2 as $value)
                      {
                        $value->setAttribute('clock_out_time', $out_time);
                        $value->save();
                      }
                    }
                    // Ends code by MOHIT GARG
                    
                    $model_time->setAttribute('created_date', $date);

                    $model_time->setAttribute('clock_in_time', $time);

                    $model_time->setAttribute('user_id', $user_id);

                    $model_time->save();

                    $access_token = Yii::$app->security->generateRandomString();
                    
                    $model_user->setAttribute('access_token', $access_token);

                    if ($model_user->save(false)) {

                        $data = [

                          'id' => $model_user->attributes['id'],

                          'email_id'=>$model_user->attributes['email_id'],

                          'role_id'=>$model_user->attributes['role_id'],

                          'access_token'=>$model_user->attributes['access_token'],

                          'first_name'=>$model_user->attributes['first_name'],

                          'last_name'=>$model_user->attributes['last_name'],

                          'gender'=>$model_user->attributes['gender'],

                          'is_active'=>$model_user->attributes['is_active'],

                          'mobile_number'=>$model_user->attributes['mobile_number'],

    //                      'user_photo'=>$model_user->attributes['user_photo'],

                        ];

                        $status = Yii::$app->common_functions->status('success_code');

                        $response = [

                            'status' => $status,

                            'data' =>array_filter($data),

    //                        'msg' => 'Password does not match'

                        ];

                        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



                    } 
                   

                }

                else

                {



                    $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

                        'msg' => 'Please enter a valid password'

                    ];

                    Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

                }

            }

            else

            {

                

                $status = Yii::$app->common_functions->status('error_code');

                $response = [

                    'status' => $status,

                    'msg' => 'Your account has been deactivated. Please contact administrator.'

                ];

                Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

            }

        }

        else

        { 

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,

                'msg' => 'Username does not match'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

        

        

    }

    



}

