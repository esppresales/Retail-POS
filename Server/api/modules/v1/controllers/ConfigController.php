<?php

/* File Description: Handles all the CRUD operations for Customer.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use yii\rest\Controller;

use Yii;

use api\modules\v1\models\Countries;

use api\modules\v1\models\Currency;

use yii\filters\VerbFilter;

use yii\db\Query;

 

class ConfigController extends Controller

{

    

    public function behaviors()

    {

//    return $behaviors;

    return [

        'verbs' => [

            'class' => VerbFilter::className(),

            'actions' => [

                'index'=>['get'],

                'view'=>['get'],

    //            '_checkAuth'=>['get'],

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

//                  'data' =>array_filter($model_user->attributes),

                'msg' => 'Method not allowed'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

            

//            $this->setHeader(400);

//            echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Method not allowed'),JSON_PRETTY_PRINT);

            exit;



        }  



          return true;  

    }

 

    /**

    * Lists all Customer models.

    * @return mixed

    */  

    public function actionIndex()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $query = new Query;

        $query->from('countries')

              ->select("*");



        $command = $query->createCommand();

        $model_countries = $command->queryAll();

        

        $query = new Query;

        $query->from('currency')
                
              ->orderBy('name asc')

              ->select("*");



        $command = $query->createCommand();

        $model_currencies = $command->queryAll();

        

        

        $data = [

            'countries' => $model_countries,

            'currencies' => $model_currencies

        ];

        

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$data,

    //                        'msg' => 'Customer created successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

  /* function to find the requested record/model */

    protected function findModel($id)

    {

        $model = Coupons::findOne($id);

        if ($model !== null) {

        return $model;

        } else {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Customer not found'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//        $this->setHeader(400);

//        echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Bad request'),JSON_PRETTY_PRINT);

        exit;

        }

    }

    /*** Function to add/edit settings ***/

    public function actionCreate()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params=$_REQUEST;

        $model_name = $params['model_name'];

        $model_name = ucfirst($model_name);

        $date = date('Y-m-d');

        if($model_name == "Discounts")

        {

            $model = Discounts::findOne(1);

            if(empty($model))

            {

                $model = new Discounts();

            }

        }

        if($model_name == "Taxes")

        {

            $model = Taxes::findOne(1);

            if(empty($model))

            {

                $model = new Taxes();

            }

        }

        if($model_name == "PeripheralConfiguration")

        {

            $model = PeripheralConfiguration::findOne(1);

            if(empty($model))

            {

                $model = new PeripheralConfiguration();

            }

        }

        if($model_name == "PrinterConfiguration")

        {

            $model = PrinterConfiguration::findOne(1);

            if(empty($model))

            {

                $model = new PrinterConfiguration();

            }

        }

        if($model_name == "ReceiptHeader")

        {

            $model = ReceiptHeader::findOne(1);

            if(empty($model))

            {

                $model = new ReceiptHeader();

            }

        }

        if($model_name == "Currency")

        {

            $model = Currency::findOne(1);

            if(empty($model))

            {

                $model = new Currency();

            }

        }

        if($model_name == "LoyaltyPoints")

        {

            $model = LoyaltyPoints::findOne(1);

            if(empty($model))

            {

                $model = new LoyaltyPoints();

            }

        }

        

        

//        $model_name = rtrim($model_name, "es");

        $model->attributes = $params;

        $model->setAttribute('created_date',$date);

        

        if ($model->save()) {

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => $model_name.' added successfully'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        } 

        else

        {

            $model_errors = $model->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

//                        'msg' => 'Category updated successfully',

                        'errors'=>$errors,

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//            $this->setHeader(400);

//            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);

        }



    }



    public function remove_arrays($errors)

    {

        $model_errors = $errors;

        foreach ($model_errors as  $key=>$value) {



            $errors[$key] = $value[0];



        }

        return [$errors];

    }



}







