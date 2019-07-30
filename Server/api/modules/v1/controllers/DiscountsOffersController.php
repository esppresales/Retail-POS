<?php

namespace api\modules\v1\controllers;
 
use Yii;
use api\modules\v1\models\Discounts;
use yii\web\Controller;
use yii\filters\VerbFilter;
use yii\db\Query;

class DiscountsOffersController extends Controller
{
    public $model = 'api\modules\v1\models\Discounts';
    
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
                 ->from('discounts')
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
        $model = Discounts::findOne($id);
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
        $params=$_REQUEST;
        $model = new Discounts();
        $model->attributes=$params;
        $date = date("Y-m-d");
        $model->setAttribute('created_date', $date);
        if ($model->save()) {
            //Added by Divya to modify thr success/error messages
            $status = Yii::$app->common_functions->status('success_code');
            $response = [
                        'status' => $status,
                        'data' =>array_filter($model->attributes),
                        'msg' => 'Discount added successfully'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
          //  $this->setHeader(200);
           // echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);
        } 
        else
        {
            //Added by Divya to modify thr success/error messages
            $model_errors = $model->errors;
            $errors = $this->remove_arrays($model_errors);
            $status = Yii::$app->common_functions->status('error_code');
            $response = [
                        'status' => $status,
                        'errors'=>$errors,
                    ];
            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
            //$this->setHeader(400);
           // echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }

    }

    
    public function actionUpdate($id)
    {                
        $params=$_REQUEST;

        $model = $this->findModel($id);
        $model->attributes=$params;
        if ($model->save()) {
             $status = Yii::$app->common_functions->status('success_code');
                    $response = [
                        'status' => $status,
                        'data' =>array_filter($model->attributes),
                        'msg' => 'Discount updated successfully'

                    ];
            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
           // $this->setHeader(200);
          //  echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);

        } 
        else
        {
            $model_errors = $model->errors;
            $errors = $this->remove_arrays($model_errors);
            $status = Yii::$app->common_functions->status('error_code');
                    $response = [
                        'status' => $status,
                        'errors'=>$errors,
                    ];
            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
            //$this->setHeader(400);
            //echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }

    }
    public function actionDelete($id)
    {
        $model=$this->findModel($id);

        if($model->delete())
        { 
        $this->setHeader(200);
        echo json_encode(array('status'=>1,'data'=>array_filter($model->attributes)),JSON_PRETTY_PRINT);

        }
        else
        {

        $this->setHeader(400);
        echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }

    }

}
