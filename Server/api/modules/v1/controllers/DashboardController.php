<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use api\modules\v1\models\Orders;

use api\modules\v1\models\Discounts;

use api\modules\v1\models\ReceiptHeader;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class DashboardController extends Controller

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

    * Lists all Dashboard Items.

    * @return mixed

    */  



    public function actionIndex()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $date = date('Y-m-d');

        $yesterday = date('Y-m-d',strtotime($date.' -1 days'));

        $week = date('Y-m-d',strtotime($date.' -8 days'));

        $month_first = date('Y-m-d',strtotime($date.' first day of last month'));

        $month_last = date('Y-m-d',strtotime($date.' last day of last month'));

        

        $query = new Query;

        $query->from('orders')

              ->where('status="completed" and created_date="'.$date.'"')

              ->select('sum(final_amount) AS todays_total');



        $command = $query->createCommand();

        $model_today = $command->queryOne();

        $todays_transactions = $query->count();

        

        $query = new Query;

        $query->from('orders')

              ->where('status="completed" and created_date="'.$yesterday.'"')

              ->select('sum(final_amount) AS yesterday_total');



        $command = $query->createCommand();

        $model_yesterday = $command->queryOne();

        $yesterday_transactions = $query->count();

        

        $query = new Query;

        $query->from('orders')

              ->where('status="completed" OR status="returned" ')

	      ->andWhere('DATE(modified_date)="'.$yesterday.'"')

              ->select('sum(return_amount) AS yesterday_return');



        $command = $query->createCommand();

        $return_yesterday = $command->queryOne();

        

        $query = new Query;

        $query->from('orders')

              ->where('created_date between "'.$week.'" and "'.$yesterday.'"')

              ->andWhere('status="completed"')

              ->select('sum(final_amount) AS lastWeek_total');



        $command = $query->createCommand();

        $model_lastWeek = $command->queryOne();

        $lastWeek_transactions = $query->count();

        

        $query = new Query;

        $query->from('orders')

              ->where('DATE(modified_date) between "'.$week.'" and "'.$yesterday.'"')

              ->andWhere('status="completed" OR status="returned"')

              ->select('sum(return_amount) AS lastWeek_return');



        $command = $query->createCommand();

        $return_lastWeek = $command->queryOne();

        

        $query = new Query;

        $query->from('orders')

              ->where('status="completed"')

              ->andWhere('created_date between "'.$month_first.'" and "'.$month_last.'"')

              ->select('sum(final_amount) AS lastMonth_total');



        $command = $query->createCommand();

        $model_lastMonth = $command->queryOne();

        $lastMonth_transactions = $query->count();

        

        $query = new Query;

        $query->from('orders')

              ->where('status="completed" OR status="returned"')

              ->andWhere('DATE(modified_date) between "'.$month_first.'" and "'.$month_last.'"')

              ->select('sum(return_amount) AS lastMonth_return');



        $command = $query->createCommand();

        $return_lastMonth = $command->queryOne();

        

        $model_discount = Discounts::findOne(1);
        $min_spend= $model_discount['min_spend'];
        if(!empty($model_discount))

        {

            if(strtotime($date) >= strtotime($model_discount['from_date']) && strtotime($date) <= strtotime($model_discount['to_date']))

            {

                $model_discount = $model_discount['percentage'];

            }

            else

            {

                $model_discount = null;

            }

        }

        else

        {

            $model_discount = null;

        }

        

        $model_store = ReceiptHeader::findOne(1);

        if(!empty($model_store))

        {            

            $model_store = $model_store['name'];          

        }

        else

        {

            $model_store = null;

        }

                

        $data = [

            'todays_total' => $model_today['todays_total'],

            'todays_transactions' => $todays_transactions,

            'yesterdays_total' => $model_yesterday['yesterday_total'],

            'yesterdays_return' => $return_yesterday['yesterday_return'],

            'yesterdays_transactions' => $yesterday_transactions,

            'lastWeek_total' => $model_lastWeek['lastWeek_total'],

            'lastWeek_return' => $return_lastWeek['lastWeek_return'],

            'lastWeek_transactions' => $lastWeek_transactions,

            'lastMonth_total' => $model_lastMonth['lastMonth_total'],

            'lastMonth_return' => $return_lastMonth['lastMonth_return'],

            'lastMonth_transactions' => $lastMonth_transactions,

            'todays_discount' => $model_discount,

            'store_name' => $model_store,
            
            'min_spend' => $min_spend

        ];

        

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$data,

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }



}

