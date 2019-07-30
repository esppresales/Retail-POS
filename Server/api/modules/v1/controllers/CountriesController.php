<?php

namespace api\modules\v1\controllers;
 
use Yii;
use api\modules\v1\models\Countries;
use yii\web\Controller;
use yii\filters\VerbFilter;
use yii\db\Query;

class CountriesController extends Controller
{
    public $model = 'api\modules\v1\models\Countries';
    
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
        $access_token = Yii::$app->request->headers['access-token'];        
        Yii::$app->common_functions->checkAccess($access_token);
        $query = new Query;
        $query->from('countries')
              ->select("id,country_name")
              ->orderby('country_name');

        $command = $query->createCommand();
        $models = $command->queryAll();

        $totalItems=$query->count();
        $status = Yii::$app->common_functions->status('success_code');
        $response = [
            'status' => $status,
            'data' =>$models,
    //                        'msg' => 'Customer created successfully'
        ];
        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
 
    }

}