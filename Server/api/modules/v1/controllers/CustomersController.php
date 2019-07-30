<?php

/* File Description: Handles all the CRUD operations for Customer.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use yii\rest\ActiveController; 

use yii\rest\Controller;

use Yii;

use api\modules\v1\models\Customers;

use api\modules\v1\models\UserInfo;

use yii\data\ActiveDataProvider;

//use yii\web\Controller;

use yii\web\NotFoundHttpException;

use yii\filters\VerbFilter;

use yii\db\Query;

 

class CustomersController extends Controller

{

    public $modelClass = 'api\modules\v1\models\Customers';

    

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

        $query=new Query;

        $query->from('customers')

                ->where('mark_as_delete = 0')

//                 ->join('INNER JOIN','user_info', 'user_info.user_id = registration.id')

                ->orderBy('id DESC')

              ->select("*");



        $command = $query->createCommand();

        $models = $command->queryAll();

        $membership_expiry = "";

        $membership_validity = "";

        $membership_type = "";

        $newModels = array();

        $i = 0;

        foreach ($models as $model) {

            $membership_validity = $model['membership_validity'];

            $membership_type = $model['membership_type'];

            $membership_expiry = $membership_validity." ".$membership_type;

            $created_date = $model['created_date'];

            $newModels[] = $model;
            //modified by Divya to fetch correct DOB
	    $newModels[$i]['dob'] = ($model['dob']!='1969-12-31')?date('d-M-Y', strtotime($model['dob'])):'null';

            $newModels[$i]['membership_expiry'] = date('d-M-Y', strtotime("+".$membership_expiry, strtotime($created_date)));;

            $query = new Query;

            $query->from('orders')

                  ->where('customer_id ='.$newModels[$i]['id'])

                  ->select("id,final_amount,status,created_date"); 

            $command = $query->createCommand();

            $newModels[$i]['transactions'] = $command->queryAll();

	    $j = 0;

            foreach ($newModels[$i]['transactions'] as $model) {



		$newModels[$i]['transactions'][$j]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
                $newModels[$i]['transactions'][$j]['status'] = ucwords($model['status']);

		$j++;

	    }

            if($newModels[$i]['earned_loyalty_points'] == null)

            {

                $newModels[$i]['earned_loyalty_points'] = 0;

            }

            

            $i++;

        }

        $totalItems=$query->count();

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$newModels,

            'totalItems' => $totalItems,

    //                        'msg' => 'Customer created successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

    /*** Function to view the details of a particular customer ***/
    
    public function actionView($id)

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $model = Customers::find()

                ->where('membership_id="'.$id.'"')
                
                ->orWhere('mobile_number="'.$id.'"')

                ->one();

        if($model == null)

        {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

                        'msg' => 'Customer not found'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        exit;

        }

        else

        {

            $membership_validity = $model->attributes['membership_validity'];

            $membership_type = $model->attributes['membership_type'];

            $created_date = $model->attributes['created_date'];

            $membership_expiry = $membership_validity." ".$membership_type;

            $membership_expiry = date('d-M-Y', strtotime("+".$membership_expiry, strtotime($created_date)));

            $model->setAttribute('membership_validity',$membership_expiry);

            if($model['earned_loyalty_points'] == null)

            {

                $model['earned_loyalty_points'] = 'null';

            }



            $customer_id = $model['id'];

            $query = new Query;



            $query->from('orders')

                  ->where('customer_id ='.$customer_id)

                  ->select("id,final_amount,status,created_date"); 

            $command = $query->createCommand();

            $transactions = $command->queryAll();

	    $j = 0;

            foreach ($transactions as $models) {
		$transactions[$j]['created_date'] = date('d-M-Y', strtotime($models['created_date']));
                $transactions[$j]['status'] = ucwords($models['status']);

		$j++;

	    }

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

                'data' =>$model->attributes,

                'transactions' => $transactions,

    //                        'msg' => 'Customer created successfully'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

    } 

//    public function actionView($id)
//
//    {
//
//        $access_token = Yii::$app->request->headers['access-token'];        
//
//        Yii::$app->common_functions->checkAccess($access_token);
//        
//        
//        
//        if (strpos($id,'_') !== false) {
//            $names = explode('_', $id);
//            $name = implode(' ', $names);
//            $first_name = $names[0];
//            $last_name = $names[1];
//        }
//        else{
//            $name = $id;
//            $first_name = $id;
//            $last_name = $id;
//        }
//        
//        $query = new Query;
//        
//        $query->from('customers')
//                
//                ->where('membership_id="'.$id.'"')
//                
//                ->orWhere('mobile_number="'.$id.'"')
//                
//                ->orWhere("(first_name || ' ' || last_name) LIKE '%".$name."%' ")
//                
//                ->orWhere("first_name LIKE '%".$first_name."%' and last_name LIKE '%".$last_name."%' ")
//                
//                ->orWhere("last_name LIKE '%".$id."%'")
//                
//                 ->orWhere("first_name LIKE '%".$id."%'")
//
//              ->select("*");
//
//
//
//        $command = $query->createCommand();
//
//        $models = $command->queryAll();
//
//        if($models == null)
//
//        {
//
//            $status = Yii::$app->common_functions->status('error_code');
//
//                    $response = [
//
//                        'status' => $status,
//
//                        'msg' => 'Customer not found'
//
//                    ];
//
//            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
//
//        exit;
//
//        }
//
//        else
//
//        {
//            
//            $newModels = array();
//            
//            $i = 0;
//            
//            foreach ($models as $model) {
//
//            $membership_validity = $model['membership_validity'];
//
//            $membership_type = $model['membership_type'];
//
//            $membership_expiry = $membership_validity." ".$membership_type;
//
//            $created_date = $model['created_date'];
//
//            $newModels[] = $model;
//
//	    $newModels[$i]['dob'] = date('d-M-Y', strtotime($model['dob']));
//
//            $newModels[$i]['membership_validity'] = date('d-M-Y', strtotime("+".$membership_expiry, strtotime($created_date)));;
//
//            $query = new Query;
//
//            $query->from('orders')
//
//                  ->where('customer_id ='.$newModels[$i]['id'])
//
//                  ->select("id,final_amount,status,created_date"); 
//
//            $command = $query->createCommand();
//
//            $newModels[$i]['transactions'] = $command->queryAll();
//
//	    $j = 0;
//
//            foreach ($newModels[$i]['transactions'] as $model) {
//
//
//
//		$newModels[$i]['transactions'][$j]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
//                $newModels[$i]['transactions'][$j]['status'] = ucwords($model['status']);
//
//		$j++;
//
//	    }
//
//            if($newModels[$i]['earned_loyalty_points'] == null)
//
//            {
//
//                $newModels[$i]['earned_loyalty_points'] = 0;
//
//            }
//
//            
//
//            $i++;
//
//        }
//            
////            foreach ($models as $model) {
////                
//////                print_r($model);exit;
////                
////            
////            $membership_validity = $model->attributes['membership_validity'];
////
////            $membership_type = $model->attributes['membership_type'];
////
////            $created_date = $model->attributes['created_date'];
////
////            $membership_expiry = $membership_validity." ".$membership_type;
////
////            $membership_expiry = date('d-M-Y', strtotime("+".$membership_expiry, strtotime($created_date)));
////
////            $model->setAttribute('membership_validity',$membership_expiry);
////
////            if($model['earned_loyalty_points'] == null)
////
////            {
////
////                $model['earned_loyalty_points'] = 'null';
////
////            }
////
////
////
////            $customer_id = $model['id'];
////
////            $query = new Query;
////
////
////
////            $query->from('orders')
////
////                  ->where('customer_id ='.$customer_id)
////
////                  ->select("id,final_amount,status,created_date"); 
////
////            $command = $query->createCommand();
////
////            $transactions = $command->queryAll();
////
////	    $j = 0;
////
////            foreach ($transactions as $models) {
////		$transactions[$j]['created_date'] = date('d-M-Y', strtotime($models['created_date']));
////                $transactions[$j]['status'] = ucwords($models['status']);
////
////		$j++;
////
////	    }
////            }
//
//            $status = Yii::$app->common_functions->status('success_code');
//
//            $response = [
//
//                'status' => $status,
//
//                'data' =>array_filter($newModels),
//
////                'transactions' => $transactions,
//
//            ];
//
//            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
//        }
//
//    } 

  /* function to find the requested record/model */

    protected function findModel($id)

    {

        $model = Customers::findOne($id);

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

    /*** Function to create a customer ***/

    public function actionCreate()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params=$_REQUEST;

        $model = new Customers();

        $model->attributes=$params;

        $dob = $model->attributes['dob'];

        $dob = date("Y-m-d", strtotime($dob));

        $date = date("Y-m-d");
        $membership_type = $params['membership_type'];
        
        if($params['membership_validity'] != null && $params['membership_validity'] != "")    
            $membership_validity = $params['membership_validity']; 
        else 
        {
            $membership_validity = 1;
            $membership_type = 'years';
        }

        $model->setAttribute('created_date', $date);
        $model->setAttribute('membership_validity', $membership_validity);
        $model->setAttribute('membership_type', $membership_type);

        $model->setAttribute('dob', $dob);

        if ($model->save()) {
           
            $dob = ($dob!='1969-12-31')?$dob:'null';      
            $model->setAttribute('dob', $dob);
            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

                        'data' =>array_filter($model->attributes),

                        'msg' => 'Customer added successfully'

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



    /*** Function to update the details of a particular customer ***/

    public function actionUpdate($id)

    {      

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

//        $params=$_REQUEST;

//        $params = file_get_contents('php://input');

        $params= Yii::$app->request->getBodyParams();

//        $params=json_decode($params);

//        print_r($params);exit;

//        print_r($_REQUEST);exit;

        $model = $this->findModel($id);

        $model->attributes=$params;

        if ($model->save()) {

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Customer updated successfully'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



//            $this->setHeader(200);

//            echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);



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

    /*** Function to delete the particular customer ***/

    public function actionDelete($id)

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $model=$this->findModel($id);

        $model->setAttribute('mark_as_delete', 1);

        if($model->save())

        { 

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Customer deleted successfully'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



        }

        else

        {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

//                        'errors' => $model->errors,

                        'msg' => 'Customer can not be deleted'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



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







