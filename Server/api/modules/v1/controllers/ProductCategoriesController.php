<?php

namespace api\modules\v1\controllers;
 
use Yii;
use yii\rest\ActiveController;
use api\modules\v1\models\ProductCategories;
use yii\data\ActiveDataProvider;
use yii\web\Controller;
use yii\filters\VerbFilter;
use yii\db\Query;

class ProductCategoriesController extends Controller
{
    public $modelClass = 'api\modules\v1\models\ProductCategories';
    
    
    public function behaviors()
    {
        return [
            'verbs' => [
                'class' => VerbFilter::className(),
                'actions' => [
                    'index'=>['get'],
                    'view'=>['get'],
                    'create'=>['post'],
                    'update'=>['post'],
                    'delete' => ['delete'],
                    'deleteall'=>['post'],
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

            $this->setHeader(400);
            echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Method not allowed'),JSON_PRETTY_PRINT);
            exit;

        }  

          return true;  
    }
 
    /**
    * Lists all User models.
    * @return mixed
    */  

    public function actionIndex()
    {
 
          $params=$_REQUEST;
          $filter=array();
          $sort="";
 
          $page=1;
          $limit=10;
 
           if(isset($params['page']))
             $page=$params['page'];
 
 
           if(isset($params['limit']))
              $limit=$params['limit'];
 
            $offset=$limit*($page-1);
 
 
            /* Filter elements */
           if(isset($params['filter']))
            {
             $filter=(array)json_decode($params['filter']);
            }
 
             if(isset($params['datefilter']))
            {
             $datefilter=(array)json_decode($params['datefilter']);
            }
 
 
            if(isset($params['sort']))
            {
              $sort=$params['sort'];
         if(isset($params['order']))
        {  
            if($params['order']=="false")
             $sort.=" desc";
            else
             $sort.=" asc";
 
        }
            }
 
 
               $query=new Query;
               $query->offset($offset)
                 ->limit($limit)
                 ->from('product_categories')
//                 ->join('INNER JOIN','user_info', 'user_info.user_id = registration.id')
                 ->orderBy($sort)
                 ->select("*");
 
           $command = $query->createCommand();
               $models = $command->queryAll();
 
               $totalItems=$query->count();
 
          $this->setHeader(200);
 
          echo json_encode(array('status'=>1,'data'=>$models,'totalItems'=>$totalItems),JSON_PRETTY_PRINT);
 
    }
    private function setHeader($status)
    {
 
        $status_header = 'HTTP/1.1 ' . $status . ' ' . $this->_getStatusCodeMessage($status);
        $content_type="application/json; charset=utf-8";

        header($status_header);
        header('Content-type: ' . $content_type);
        header('X-Powered-By: ' . "Nintriva <nintriva.com>");
    }
    private function _getStatusCodeMessage($status)
    {
        $codes = Array(
        200 => 'OK',
        400 => 'Bad Request',
        401 => 'Unauthorized',
        402 => 'Payment Required',
        403 => 'Forbidden',
        404 => 'Not Found',
        500 => 'Internal Server Error',
        501 => 'Not Implemented',
        );
        return (isset($codes[$status])) ? $codes[$status] : '';
    }
    public function actionView($id)
    {
        $model=$this->findModel($id);

        $this->setHeader(200);
        echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);

    } 
  /* function to find the requested record/model */
    protected function findModel($id)
    {
        $model = ProductCategories::findOne($id);
        if ($model !== null) {
        return $model;
        } else {

        $this->setHeader(400);
        echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Bad request'),JSON_PRETTY_PRINT);
        exit;
        }
    }
    public function actionCreate()
    {
//        echo 'hi';exit;
        $params=$_REQUEST;
        $model = new ProductCategories();
        $model->attributes=$params;
        $date = date("Y-m-d");
        $model->setAttribute('created_date', $date);
        if ($model->save()) {
            $this->setHeader(200);
            echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);
        } 
        else
        {
            $this->setHeader(400);
            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }

    }

    
    public function actionUpdate($id)
    {                
        $params=$_REQUEST;

        $model = $this->findModel($id);
        $model->attributes=$params;
        if ($model->save()) {

            $this->setHeader(200);
            echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);

        } 
        else
        {
            $this->setHeader(400);
            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }

    }
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
                        'msg' => 'Product category deleted successfully'
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
                        'errors' => $errors,
                    ];
            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        }

    }

}
