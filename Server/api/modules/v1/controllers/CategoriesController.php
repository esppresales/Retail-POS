<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use api\modules\v1\models\ProductCategories;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class CategoriesController extends Controller

{

    public $model = 'api\modules\v1\models\ProductCategories';

    

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

    * Lists all Category models.

    * @return mixed

    */  



    public function actionIndex()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $query = new Query;

        $query->from('product_categories')

                 ->join('LEFT JOIN','products', 'products.category_id = product_categories.id')

                 ->groupBy('product_categories.id')

                ->orderBy('product_categories.id DESC')

              ->select("product_categories.id, product_categories.name,product_categories.description, COUNT(products.id) AS product_count, product_categories.mark_as_delete");



        $command = $query->createCommand();

        $models = $command->queryAll();



        $totalItems=$query->count();

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$models,

            'totalItems'=>$totalItems,

//                        'msg' => 'Customer updated successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

    /*** Function to view the details of a particular product category ***/

    public function actionView($id)

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $model=$this->findModel($id);

        

        $totalItems=$this->check_products($id);

        $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

                        'data' =>[

                                    array_filter($model->attributes),

                                    'product_count'=>$totalItems],

                        'quantity'=>$totalItems,

//                        'msg' => 'Password does not match'

                    ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



    } 

  /* function to find the requested record/model */

    protected function findModel($id)

    {

        $model = ProductCategories::findOne($id);

        if ($model !== null) {

        return $model;

        } else {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Category Id not found'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        exit;

        }

    }

    /*** Function to add a new product category ***/

    public function actionCreate()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params=$_REQUEST;

        $model = new ProductCategories();

        $model->attributes=$params;

        $date = date("Y-m-d");

        $model->setAttribute('created_date', $date);

        if ($model->save()) {

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

                        'data' =>array_filter($model->attributes),

                        'msg' => 'Category added successfully'

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

                        'errors' => $errors,

//                        'msg' => 'Password does not match'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }



    }



    /*** Function to update the details of a particular product category ***/

    public function actionUpdate($id)

    {       

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        Yii::$app->common_functions->_parsePut();

        $params = $GLOBALS['_PUT'];

        $model = $this->findModel($id);

        $model->attributes=$params;
        
        $mark_as_delete = isset($params['mark_as_delete'])?$params['mark_as_delete']:'';
        if($mark_as_delete == '0')
        {
            $var = 'enabled';
        }
        else
        {
            $var = 'updated';
        }

        if ($model->save()) {

            $msg = 'Category '.$var.' successfully';

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

                        'data' =>array_filter($model->attributes),

                        'msg' => $msg

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

                        'errors'=>$errors,

//                        'msg' => 'Password does not match'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }



    }

    /*** Function to delete the particular product category ***/

    public function actionDelete($id)

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $model=$this->findModel($id);

        $quantity = $this->check_products($id);
        $model->setAttribute('mark_as_delete', 1);
        if($quantity == 0)

        {

        if($model->save())

        {

            $status = Yii::$app->common_functions->status('success_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Category disabled successfully'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));



        }

        else

        {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Category could not be disabled'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

        }

        else{

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'This category is linked to some products. So, it cannot be disabled.'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }



    }

    

    public function check_products($id)

    {

        $model=$this->findModel($id);

        $query = new Query;

        $query->from('products')

                 ->join('INNER JOIN','product_categories', 'products.category_id = product_categories.id')

                ->where('products.category_id = '.$id)

              ->select("count(*)");

        $totalItems=$query->count();

        return $totalItems;

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

