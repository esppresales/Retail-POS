<?php

/* File Description: Handles the Logout operation for User.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use yii\rest\ActiveController; 

use Yii;

use api\modules\v1\models\Users;

use api\modules\v1\models\LoginForm;

use api\modules\v1\models\TimeTracking;

use yii\data\ActiveDataProvider;

use yii\web\Controller;

use yii\web\NotFoundHttpException;

use yii\filters\VerbFilter;



class LogoutController extends Controller

{

    public $model = 'api\modules\v1\models\Users';

    

    public function behaviors()

    {

    return [

        'verbs' => [

            'class' => VerbFilter::className(),

            'actions' => [

                'signout'=>['get'],

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

            exit;



        }  



        return true;  

    }

 

    /*** Function to handle the logging out of the user ***/

    public function actionSignout() {

        

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $model_user = new Users;

        $model_time = new TimeTracking;

//        $access_token = $_REQUEST['access_token'];
        $timezone = @date_default_timezone_get();
//        $time = @gmdate("h:i:sa");
        $time = @gmdate("Y-m-d H:i:s"); // CHanged to complete date from time only by Mohit Garg for time tracking issue
        //$time = date("h:i:sa");

        $date = date("Y-m-d");

        $model_user = $model_user->findByAccessToken($access_token);

        $user_id = $model_user->attributes['id'];

        $model_time = TimeTracking::find()

                    ->where(['user_id' => $user_id

                             ])

                    ->orderby('id Desc')

                    ->one();

        if($model_user != Null && $model_time != NULL)

        {

            $model_time->setAttribute('clock_out_time', $time);

            $model_time->save();

            $access_token = "";

            $model_user->setAttribute('access_token', $access_token);
            //modified by divya - added false
            if ($model_user->save(false)) {

                

                $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model_user->attributes),

                        'msg' => 'Logged out successfully'

                    ];

                Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



            }

        }

        else

        {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model_user->attributes),

                        'msg' => 'Login first'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

    }



}

