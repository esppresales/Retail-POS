<?php

/* File Description: Handles all the CRUD operations for Customer.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

use yii\rest\ActiveController;
use yii\rest\Controller;
use Yii;
use api\modules\v1\models\Orders;
use api\modules\v1\models\OrderItems;
use api\modules\v1\models\Products;
use api\modules\v1\models\Customers;
use api\modules\v1\models\CustomerRedeemHistory;
use api\modules\v1\models\LoyaltyPointsHistory;
use yii\data\ActiveDataProvider;
//use yii\web\Controller;
use yii\web\NotFoundHttpException;
use yii\filters\VerbFilter;
use yii\db\Query;

class ReportsController extends Controller {

//    public $modelClass = 'api\modules\v1\models\Orders';

    public function behaviors() {

//    return $behaviors;

        return [

            'verbs' => [

                'class' => VerbFilter::className(),
                'actions' => [

                    'index' => ['get'],
                    'view' => ['get'],
                    //            '_checkAuth'=>['get'],
                    'create' => ['post'],
                    'update' => ['put'],
                    'delete' => ['delete'],
                    'return' => ['post'],
                ],
            ],
        ];
    }

    public function beforeAction($event) {

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
    public function actionIndex() {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);
        
        $params = $_REQUEST;
        $params['date'] = date('Y-m-d',  strtotime($params['date']));
        
        $query = new Query;
        $query->select(['order_items.*','orders.receipt_no','orders.tax_percentage','orders.discount_percentage','orders.coupon_discount_amount','products.name','products.selling_price','(products.selling_price * qty) as total_price',
            "(select sum(qty) from order_items join orders on orders.id=order_items.order_id where date(order_items.created_date) = '".$params['date']."'  and orders.status = 'completed')as max_qty",
            "(SELECT count(*) from orders where created_date =  '".$params['date']."'   and status = 'completed')as total_transaction",
            "(select sum(final_amount) from orders where date(created_date) =  '".$params['date']."'  and payment_type= 'Nets'  and status = 'completed') as nets_amount",
            "(select sum(final_amount) from orders where date(created_date) =  '".$params['date']."'  and payment_type= 'Cash'  and status = 'completed') as cash_amount",
            "(select sum(final_amount) from orders where date(created_date) =  '".$params['date']."'  and payment_type= 'Visa/Master'  and status = 'completed') as card_amount"])
                                ->from('order_items')                  
                                ->join('JOIN', 'orders', 'orders.id = order_items.order_id')
                                ->join('JOIN', 'products', 'order_items.product_id = products.id ')
                                ->where("date(orders.created_date) = '".$params['date']."'")
                                ->andWhere("orders.status = 'completed'");
                                
        $command = $query->createCommand();

        $result = $command->queryAll();

//        $count = $query->count();
        
        
        
        $count= ($result) ? $result[0]['total_transaction'] : 0;
        $max_qty= ($result) ? $result[0]['max_qty'] : 0;
        $nets_total_sum= ($result) ? $result[0]['nets_amount'] : 0;
        $cash_total_sum= ($result) ? $result[0]['cash_amount'] : 0;
        $card_total_sum= ($result) ? $result[0]['card_amount'] : 0;
        $total_sum= $nets_total_sum+ $cash_total_sum +$card_total_sum;
        $gst_inclusive = 0;
        $amount_includes_gst = 0;
        $amount_before_gst =0;
        
        $final_result = [];       
        $i =0 ;
        foreach($result as  $key=>$value)
        {
            $final_result[$value['product_id']]['product_id'] = $value['product_id'];
            $final_result[$value['product_id']]['name'] = $value['name'];
            $final_result[$value['product_id']]['orders'][$value['order_id']]['order_id'] = 'OR'.sprintf("%012d", $value['order_id']);
            $final_result[$value['product_id']]['orders'][$value['order_id']]['receipt_no']  = 'RE'.sprintf("%06d", $value['receipt_no']);
            $final_result[$value['product_id']]['orders'][$value['order_id']]['order_qty']  = $value['qty'];
            $final_result[$value['product_id']]['orders'][$value['order_id']]['selling_price'] = $value['selling_price'];
            
            $gross_amount = $value['qty'] * $value['product_price'];
            
            $gst_amount = round(($gross_amount) / (1 + ($value['tax_percentage'] / 100)), 2);
           $amount_before_gst = $amount_before_gst+ $gst_amount;
            $before_gst_amount = $gross_amount - $gst_amount;
                 
            $gst_inclusive = $gst_inclusive + $before_gst_amount;
            
            $discount_amount = round((($value['discount_percentage'] * $gst_amount) / 100), 2);
            $coupon_amount = ($value['coupon_discount_amount']);
            
            $total_discount = $discount_amount + $coupon_amount;
            
            if($total_discount > 0)
            {

            $gst_amount = ($gst_amount) - ( $total_discount);


            $after_gst_amount = round((($value['tax_percentage'] * ($gst_amount)) / 100), 2);
            
            
            
            $final_amount = round(($after_gst_amount + $gst_amount), 2);
            }
            else{
                $final_amount = round(($gross_amount), 2); 
            }
                    
            
//            $final_result[$value['product_id']]['orders'][$value['order_id']]['total_price'] = $value['total_price'];
            $final_result[$value['product_id']]['orders'][$value['order_id']]['total_price'] = $final_amount;
            $final_result[$value['product_id']]['orders'][$value['order_id']]['discount_price'] = $discount_amount;
            
            
            $i++;
            $final_result[$value['product_id']]['orders'] = array_values($final_result[$value['product_id']]['orders']);
        }
         $final_result = array_values($final_result);        
         $data['day_sales_report'] = $final_result;
         $data['total_transactions'] = intval($count);
         $data['total_qty'] = intval($max_qty);
         $data['total_sum'] = $total_sum;
         $data['cash_total_sum'] = $cash_total_sum;
         $data['nets_total_sum'] = $nets_total_sum;
         $data['card_total_sum'] = $card_total_sum;
         $data['gst_inclusive'] =  $gst_inclusive;
         $data['amount_includes_gst'] = $total_sum;
         $data['amount_before_gst'] = $amount_before_gst;
//        echo '<pre>';print_r($data);exit;
        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,
            'data' => $data,
        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//        $this->setHeader(200);
//        echo json_encode(array('status'=>0,'data' => 'array_filter($model->attributes)','message'=>'Customer created successfully'),JSON_PRETTY_PRINT);
    }

    /* function to find the requested record/model */

    protected function findModel($id) {

        $model = Orders::findOne($id);

        if ($model !== null) {

            return $model;
        } else {

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'msg' => 'Order not found'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//        $this->setHeader(400);
//        echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Bad request'),JSON_PRETTY_PRINT);

            exit;
        }
    }



}
