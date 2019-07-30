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

class OrdersController extends Controller {

    public $modelClass = 'api\modules\v1\models\Orders';

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

        $query = new Query;

        $query->from('orders')
                ->orderBy('id DESC')
                ->select("*");



        $command = $query->createCommand();

        $models = $command->queryAll();



        $newModels = array();

        $i = 0;

        foreach ($models as $model) {



            $newModels[] = $model;
            $newModels[$i]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
            $newModels[$i]['id'] = sprintf("%012d", $newModels[$i]['id']);

            $query = new Query;

            $query->from('products')
                    ->join('INNER JOIN', 'order_items', 'order_items.product_id = products.id')
                    ->join('INNER JOIN', 'orders', 'orders.id = order_items.order_id')
                    ->join('LEFT JOIN', 'customers', 'customers.id = orders.customer_id')
                    ->where('order_items.order_id = ' . $newModels[$i]['id'])
                    ->select("customers.membership_id, customers.earned_loyalty_points, products.id, products.name, order_items.product_price, order_items.qty, order_items.return_qty");



            $command = $query->createCommand();

            $newModels[$i]['product_details'] = $command->queryAll();



            if (!empty($newModels[$i]['product_details'])) {

                $membership_id = $newModels[$i]['product_details'][0]['membership_id'];

                $newModels[$i]['membership_id'] = $membership_id;

                $loyalty_points = $newModels[$i]['product_details'][0]['earned_loyalty_points'];

                $newModels[$i]['earned_loyalty_points'] = $loyalty_points;
            } else {

                $newModels[$i]['membership_id'] = 'null';

                $newModels[$i]['earned_loyalty_points'] = '0';

                if ($newModels[$i]['earned_loyalty_points'] == null || empty($newModels[$i]['earned_loyalty_points'])) {
                    $newModels[$i]['earned_loyalty_points'] = '0';
                }
            }


            $query = new Query;

            $query->from('customer_redeem_history')
                    ->where('order_id = ' . $newModels[$i]['id'])
                    ->select("redeem_points");



            $command = $query->createCommand();

            $redeem_points = $command->queryOne();



            if ($redeem_points['redeem_points'] == null) {

                $redeem_points['redeem_points'] = 0;
            }


            $newModels[$i]['redeemed_points'] = $redeem_points['redeem_points'];

//            unset($newModels[$i]['product_details'][0]['membership_id']);

            $i++;
        }


        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,
            'data' => $newModels,
//                        'msg' => 'Customer created successfully'
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

    public function actionView($id) {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $model = $this->findModel($id);



        $query = New Query;

        $query->from('products')
                ->join('INNER JOIN', 'order_items', 'order_items.product_id = products.id')
                ->where('order_items.order_id = ' . $id)
                ->select("products.id, products.name, order_items.product_price, order_items.qty, order_items.return_qty");

        $command = $query->createCommand();

        $model_product = $command->queryAll();



        $newModel = [

            'order_details' => array_filter($model->attributes),
            'product_details' => $model_product
        ];



        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,
            'data' => $newModel,
//                        'msg' => 'Customer created successfully'
        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    }

    /*     * * Function to add a new order ** */

    public function actionCreate() {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $params = $_REQUEST;

//        print_r($params);exit;

        if (!empty($params['order_id'])) {

            $order_id = $params['order_id'];

            $model_order = $this->findModel($order_id);



            $query = new Query;

            $query->from('order_items')
                    ->where('order_id=' . $order_id)
                    ->select('id,product_id');

            $command = $query->createCommand();

            $id_orderItems = $command->queryAll();

            $query = new Query;

            // Removed Manual Query Because it was giving setAttribute error by Mohit Garg
//      $query->from('customer_redeem_history')
//              ->where('order_id=' . $order_id)
//              ->select('*');
//
//      $command = $query->createCommand();
//
//      $model_customerRedeemHistory = $command->queryAll();

            $model_customerRedeemHistory = CustomerRedeemHistory::findOne([
                        'order_id' => $order_id,
            ]);

            // Ends

            if (!count($model_customerRedeemHistory)) {
                $model_customerRedeemHistory = new CustomerRedeemHistory();
            }
        } else {

            $model_order = new Orders();

            $model_customerRedeemHistory = new CustomerRedeemHistory();
        }


        $model_loyaltyPointsHistory = new LoyaltyPointsHistory();

        $model_orderItems = new OrderItems();
        $model_order->attributes = $params;
        $model_order->payment_type = $params['payment_type'];

        $model_orderItems->attributes = $params;

        $order_status = $model_order->attributes['status'];

        $membership_id = @$params['membership_id'];



        $coupon_code = $model_order->attributes['coupon_code'];

        $product_id = $model_orderItems->attributes['product_id'];

        $product_id = explode(',', $product_id);

        $product_qty = $model_orderItems->attributes['qty'];

        $product_qty = explode(',', $product_qty);

        $current_date = date("Y-m-d");

        // Check extra existing product on case of update and delete them (MOHIT GARG)
        $products = array();
        if (!empty($id_orderItems)) {
            foreach ($id_orderItems as $value) {
                array_push($products, $value['product_id']);
            }
            // Remove product from order
            $extra_existing_products = array_diff($products, $product_id);
            foreach ($extra_existing_products as $value) {
                $model_orderItems = OrderItems::find()->where(['product_id' => $value, 'order_id' => $params['order_id']])->one();
                $model_orderItems->delete();
            }
        }
        // Ends

        $query = new Query;

        $query->from('customers')
                ->where('membership_id="' . $membership_id . '"')
                ->select("*");

        $command = $query->createCommand();

        $customer_details = $command->queryOne();

        $model_customer = Customers::findOne($customer_details['id']);

        $customer_loyalty_points = $model_customer['earned_loyalty_points'];



        $query = new Query;

        $query->from('discounts')
                ->select("*");

        $command = $query->createCommand();

        $model_discount = $command->queryOne();



        $query = new Query;

        $query->from('taxes')
                ->select("*");

        $command = $query->createCommand();

        $model_tax = $command->queryOne();



        $query = new Query;

        $query->from('coupons')
                ->where('coupon_code = "' . $coupon_code . '"')
                ->select("*");

        $command = $query->createCommand();

        $model_coupon = $command->queryOne();



        $query = new Query;

        $query->from('loyalty_points')
                ->select("*");

        $command = $query->createCommand();

        $model_loyalty = $command->queryOne();



        $i = 0;

        $gross_amount = 0;

        foreach ($product_id as $id) {



            $query = new Query;

            $query->from('products')
                    ->where('id = ' . $id)
                    ->select("selling_price");



            $command = $query->createCommand();

            $model_product = $command->queryOne();

            $gross_amount = ($product_qty[$i] * $model_product['selling_price']) + $gross_amount;

            $i++;
        }



        if (strtotime($current_date) >= strtotime($model_discount['from_date']) && strtotime($current_date) <= strtotime($model_discount['to_date'])) {

            $discount = $model_discount['percentage'];

            if ($model_discount['min_restriction'] == 1) {

                if ($gross_amount >= $model_discount['min_spend']) {

                    $discount = $model_discount['percentage'];
                } else {

                    $discount = 0;
                }
            } else {

                $discount = 0;
            }
        } else {

            $discount = 0;
        }



        if (strtotime($current_date) >= strtotime($model_coupon['validity_from_date']) && strtotime($current_date) <= strtotime($model_coupon['validity_to_date'])) {

            $coupon_amount = $model_coupon['amount'];
        } else {

            $coupon_amount = 0;
        }



        if (!empty($model_customer)) {

            $customer_loyalty_points = $customer_details['earned_loyalty_points'];

            $loyalty = @$params['redeemed_points'];

            if ($loyalty <= $customer_loyalty_points) {

                $loyalty = $loyalty;
            } else {

                $loyalty = $customer_loyalty_points;
            }
        } else {

            $loyalty = 0;
        }




//
//        //$tax_amount = ($model_tax['percentage']*($gross_amount - $discount_amount))/100;
        $tax_amount = ($model_tax['percentage'] * ($gross_amount)) / 100;
//
//        $final_amount = ($gross_amount + $tax_amount) - ($coupon_amount + $loyalty + $discount_amount);
//
//        echo '<pre>';print_r($gross_amount);exit;

        $gst_amount = round(($gross_amount) / (1 + ($model_tax['percentage'] / 100)), 2);

        $before_gst_amount = $gross_amount - $gst_amount;

        $discount_amount = round((($discount * $gst_amount) / 100), 2);

        $total_discounts = $coupon_amount + $loyalty + $discount_amount;

        $gst_amount = ($gst_amount) - ($coupon_amount + $loyalty + $discount_amount);
        if ($total_discounts > 0) {
            $after_gst_amount = round((($model_tax['percentage'] * ($gst_amount)) / 100), 2);

            $final_amount = round(($after_gst_amount + $gst_amount), 2);
        } else {
            $final_amount = round(($gross_amount), 2);
        }


        $model_order->setAttribute('gross_amount', $gross_amount);

        $model_order->setAttribute('final_amount', $final_amount);

        $model_order->setAttribute('created_date', $current_date);

        $model_order->setAttribute('tax_percentage', $model_tax['percentage']);

        $model_order->setAttribute('tax_amount', $tax_amount);

        $model_order->setAttribute('coupon_code', $coupon_code);

        $model_order->setAttribute('coupon_discount_amount', $coupon_amount);

        $model_order->setAttribute('discount_percentage', $discount);

        $model_order->setAttribute('discount_amount', $discount_amount);

//        $model_order->setAttribute('customer_id',$customer_id);





        if (!empty($model_customer)) {

            if ($model_loyalty['is_loyalty_on'] == '1') {

                $loyalty_amount = $model_loyalty['earned_amount'];

                $final_loyalty_points = round($final_amount / $loyalty_amount);
            } else {

                $final_loyalty_points = 0;
            }



            $model_order->setAttribute('customer_id', $customer_details['id']);

            $customer_loyalty_points = $customer_loyalty_points + $final_loyalty_points - $loyalty;

            $customer_loyalty_points = round($customer_loyalty_points);

            $model_customer->setAttribute('earned_loyalty_points', $customer_loyalty_points);
        }
//        print_r($model_customer);exit;


        if ($model_order->save()) {



            if ($order_status == 'completed') {

                $max_receipt_no = Orders::find()->select(['max(receipt_no) as receipt_no'])->one();
                $receipt_no = (empty($max_receipt_no->receipt_no)) ? 1 : $max_receipt_no->receipt_no + 1;
                $connection = Yii::$app->db;
                $connection->createCommand()
                        ->update('orders', ['receipt_no' => $receipt_no], "id = " . $model_order->attributes['id'] . "")
                        ->execute();

                if (!empty($model_customer)) {

                    $model_loyaltyPointsHistory->setAttribute('order_id', $model_order->attributes['id']);

                    $model_loyaltyPointsHistory->setAttribute('customer_id', $customer_details['id']);

                    $model_loyaltyPointsHistory->setAttribute('amount_spend', $final_amount);

                    $model_loyaltyPointsHistory->setAttribute('earned_points', $final_loyalty_points);

                    $model_loyaltyPointsHistory->setAttribute('created_date', $current_date);

                    $model_loyaltyPointsHistory->save();

//                      $max_receipt_no = Orders::find()->select(['max(receipt_no) as receipt_no'])->one();
//                        if(empty($max_receipt_no->receipt_no))
//                            {
//                            $max_receipt_no->receipt_no = 1;
//                            
//                        }
//                        else{
//                            $max_receipt_no->receipt_no = $max_receipt_no->receipt_no+1;
//                            $model_order->setAttribute('receipt_no', $max_receipt_no->receipt_no);
//                        }

                    if ($params['redeemed_points']) {
                        if (!empty($model_customerRedeemHistory)) {
                            $model_customerRedeemHistory->delete();
                        }
                        $model_customerRedeemHistory->setAttribute('order_id', $model_order->attributes['id']);

                        $model_customerRedeemHistory->setAttribute('customer_id', $customer_details['id']);

                        $model_customerRedeemHistory->setAttribute('created_date', $current_date);

                        $model_customerRedeemHistory->setAttribute('redeem_points', $params['redeemed_points']);

                        $model_customerRedeemHistory->setAttribute('points_amount', $loyalty);

                        $model_customerRedeemHistory->save();

                        $model_customer->save();
                    } else {

                        $model_customerRedeemHistory->setAttribute('order_id', $model_order->attributes['id']);

                        $model_customerRedeemHistory->setAttribute('customer_id', $customer_details['id']);

                        $model_customerRedeemHistory->setAttribute('created_date', $current_date);

                        $model_customerRedeemHistory->setAttribute('redeem_points', $params['redeemed_points']);

                        $model_customerRedeemHistory->setAttribute('points_amount', $loyalty);

                        $model_customerRedeemHistory->save();

                        $model_customer->save();
                    }
                }



                $i = 0;



                foreach ($product_id as $value) {

                    $model_product = Products::findOne($value);

                    $qty = $model_product->attributes['quantity'] - $product_qty[$i];

                    $model_product->setAttribute('quantity', $qty);

                    $model_orderItems->setAttribute('product_id', $value);

                    $model_orderItems->setAttribute('order_id', $model_order->attributes['id']);

                    $model_orderItems->setAttribute('qty', $product_qty[$i]);

                    $model_orderItems->setAttribute('product_price', $model_product['selling_price']);

                    $model_orderItems->setAttribute('created_date', $current_date);

                    $model_product->save();

                    if (!empty($id_orderItems)) {

                        foreach ($id_orderItems as $orderitem) {

                            if ($orderitem['product_id'] == $value) {

                                $model_orderItems = OrderItems::findOne($orderitem['id']);

                                $model_orderItems->setAttribute('product_id', $value);

                                $model_orderItems->setAttribute('order_id', $model_order->attributes['id']);

                                $model_orderItems->setAttribute('qty', $product_qty[$i]);

                                $model_orderItems->setAttribute('product_price', $model_product['selling_price']);

                                $model_orderItems->setAttribute('created_date', $current_date);



                                $model_orderItems->save();
                            }
                        }
                    } else {

                        $model_orderItems->save();

                        $model_orderItems = new OrderItems();
                    }

                    $i++;
                }
            }

            if ($order_status == 'on hold') {
                $max_receipt_no = Orders::find()->select(['max(receipt_no) as receipt_no'])->one();
                $connection = Yii::$app->db;
                $connection->createCommand()
                        ->update('orders', ['receipt_no' => $max_receipt_no->receipt_no], "id = " . $model_order->attributes['id'] . "")
                        ->execute();

                if (!empty($model_customer)) {
                    if ($params['redeemed_points'] == '0') {
                        if (!empty($model_customerRedeemHistory)) {
                            $model_customerRedeemHistory->delete();
                        }
                    } else {

                        $model_customerRedeemHistory->setAttribute('order_id', $model_order->attributes['id']);

                        $model_customerRedeemHistory->setAttribute('customer_id', $customer_details['id']);

                        $model_customerRedeemHistory->setAttribute('created_date', $current_date);

                        $model_customerRedeemHistory->setAttribute('redeem_points', $params['redeemed_points']);

                        $model_customerRedeemHistory->setAttribute('points_amount', $loyalty);

                        $model_customerRedeemHistory->save();
                    }
                }

                $i = 0;

                foreach ($product_id as $value) {
                    // Set product id
                    $p_id = 0;

                    $model_product = Products::findOne($value);

                    $qty = $model_product->attributes['quantity'] - $product_qty[$i];

                    $model_product->setAttribute('quantity', $qty);

                    $model_orderItems->setAttribute('product_id', $value);

                    $model_orderItems->setAttribute('order_id', $model_order->attributes['id']);

                    $model_orderItems->setAttribute('qty', $product_qty[$i]);

                    $model_orderItems->setAttribute('product_price', $model_product['selling_price']);

                    $model_orderItems->setAttribute('created_date', $current_date);


//                    $model_product->save();

                    if (!empty($id_orderItems)) {

                        foreach ($id_orderItems as $orderitem) {

                            if ($orderitem['product_id'] == $value) {

                                $model_orderItems = OrderItems::findOne($orderitem['id']);

                                $model_orderItems->setAttribute('product_id', $value);

                                $model_orderItems->setAttribute('order_id', $model_order->attributes['id']);

                                $model_orderItems->setAttribute('qty', $product_qty[$i]);

                                $model_orderItems->setAttribute('product_price', $model_product['selling_price']);

                                $model_orderItems->setAttribute('created_date', $current_date);

                                $p_id++;


                                $model_orderItems->save();
                            }
                        }
                        // Check if this item is newly added in an order update (MOHIT GARG)
                        if ($p_id == 0) {

                            $t = array_search($id, $product_id);

                            $model_orderItems2 = new OrderItems();

                            $model_orderItems2->setAttribute('product_id', $id);

                            $model_orderItems2->setAttribute('order_id', $model_order->attributes['id']);

                            $model_orderItems2->setAttribute('qty', $product_qty[$t]);

                            $model_orderItems2->setAttribute('product_price', $model_product['selling_price']);

                            $model_orderItems2->setAttribute('created_date', date("Y-m-d"));

                            $model_orderItems2->save();
                        }
                        // Ends
                    } else {

                        $model_orderItems->save();

                        $model_orderItems = new OrderItems();
                    }

//                    $model_orderItems->save();
//
//                    $model_orderItems = new OrderItems();

                    $i++;
                }
            }

            if ($order_status == 'cancelled') {
                $max_receipt_no = Orders::find()->select(['max(receipt_no) as receipt_no'])->one();
                $connection = Yii::$app->db;
                $connection->createCommand()
                        ->update('orders', ['receipt_no' => $max_receipt_no->receipt_no], "id = " . $model_order->attributes['id'] . "")
                        ->execute();
                if (!empty($id_orderItems)) {

                    foreach ($id_orderItems as $value) {

                        $model_orderItems = OrderItems::findOne($value['id']);

                        $model_orderItems->delete();
                    }
                }
                if (!empty($model_customer) && !empty($model_customerRedeemHistory)) {

                    $model_customerRedeemHistory->delete();
                }
            }
            $receipt_no_final = Orders::find()->select(['receipt_no'])->where(['=', 'id', $model_order->id])->one();
            $order_id = sprintf("%012d", $model_order->id);
            $receipt_no_final = sprintf("%06d", $receipt_no_final->receipt_no);

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
                'data' => 'OR' . $order_id,
                'receipt_no' => 'RE' . $receipt_no_final,
                'msg' => 'Order ' . $order_status . ' successfully'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        } else {

            $model_errors = $model_order->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
//                        'msg' => 'Category updated successfully',
                'errors' => $errors,
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//            $this->setHeader(400);
//            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }
    }

    /*     * * Function to update the details of a particular customer ** */

    public function actionUpdate($id) {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        Yii::$app->common_functions->_parsePut();

        $params = $GLOBALS['_PUT'];

        $model_order = $this->findModel($id);

        $model_order->attributes = $params;

        $discount_percentage = $model_order->attributes['discount_percentage'];

        $coupon_discount_amount = $model_order->attributes['coupon_discount_amount'];

        $coupon_code = $model_order->attributes['coupon_code'];

        $tax_percentage = $model_order->attributes['tax_percentage'];



        if ($model_order['customer_id'] != 0) {

            $query = new Query;

            $query->from('customers')
                    ->where('id="' . $model_order['customer_id'] . '"')
                    ->select("*");

            $command = $query->createCommand();

            $customer_details = $command->queryOne();

            $model_customer = Customers::findOne($customer_details['id']);

            $customer_loyalty_points = $customer_details['earned_loyalty_points'];



            $query = new Query;

            $query->from('loyalty_points')
                    ->select("*");

            $command = $query->createCommand();

            $model_loyalty = $command->queryOne();



            $query = new Query;

            $query->from('loyalty_points_history')
                    ->where('order_id = ' . $id)
                    ->orderby('id DESC')
                    ->select("*");

            $command = $query->createCommand();

            $loyaltyPointsHistory_details = $command->queryOne();

            $model_loyaltyPointsHistory = LoyaltyPointsHistory::findOne($loyaltyPointsHistory_details['id']);
        }



        $product_id = $params['product_id'];

        $product_id = explode(',', $product_id);

        $product_qty = $params['product_qty'];

        $product_qty = explode(',', $product_qty);

        $return_product_qty = array_sum($product_qty);



        $query = New Query;

        $query->from('order_items')
                ->where('order_id = ' . $id)
                ->select('*');

        $command = $query->createCommand();

        $orderItems_details = $command->queryAll();



        $k = 0;

        $total_no_of_products = 0;

        foreach ($orderItems_details as $value) {



            $total_no_of_products = $total_no_of_products + $orderItems_details[$k]['qty'] - $orderItems_details[$k]['return_qty'];

            $k++;
        }



        $query->from('customer_redeem_history')
                ->where('order_id = ' . $id)
                ->select('*');

        $command = $query->createCommand();

        $customerRedeem_details = $command->queryOne();



        $loyalty_amount = $customerRedeem_details['points_amount'];
        $redeem_point = $customerRedeem_details['redeem_points'];

        $model_customerRedeem = CustomerRedeemHistory::findOne($customerRedeem_details['id']);

//        echo $loyalty_amount;exit;

        foreach ($orderItems_details as $value) {

            $orderItem_id[] = $value['id'];
        }

        $model_orderItems = OrderItems::findAll($orderItem_id);

        $i = 0;

        // $if_return added by Mohit for amount problem on product return in complete function
        $if_return = 0;

        $return_amt = 0;

        foreach ($product_id as $id) {

            $model_product = Products::findOne($id);

            foreach ($model_orderItems as $orderItems) {

                if ($orderItems['product_id'] == $id) {

                    $new_product_qty[$i] = $orderItems['return_qty'] + $product_qty[$i];

                    $orderItems->setAttribute('is_returned', 1);

                    $orderItems->setAttribute('return_qty', $new_product_qty[$i]);

                    $orderItems->save();

                    $if_return++;

                    $return_amt = $product_qty[$i] * $orderItems->attributes['product_price'] + $return_amt;
                }
            }

            $qty = $model_product->attributes['quantity'] + $product_qty[$i];

            $model_product->setAttribute('quantity', $qty);

            $model_product->save();

            $i++;
        }

        // If condition applied by Mohit for price problem on return product
        if ($if_return == 0) {
            $discount_amount = ($discount_percentage * $model_order['gross_amount']) / 100;
        } else {
            $discount_percentage = 0;
            $discount_amount = 0;
        }

        $gross_amount = $model_order['gross_amount'] - $return_amt;

        $tmp_var = $total_no_of_products / $return_product_qty;

        // If condition applied by Mohit for price problem on return product
        if ($if_return == 0) {
            $coupon_amount = $coupon_discount_amount;
        } else {
            $coupon_code = '';
            $coupon_amount = 0;
        }
//        $coupon_amount = $coupon_discount_amount/$tmp_var;
//
//        $coupon_amount = $coupon_discount_amount - $coupon_amount;
        // If condition applied by Mohit for price problem on return product
        if ($if_return == 0) {
            $loyalty_amount1 = $loyalty_amount / $tmp_var;

            $loyalty_amount1 = $loyalty_amount - $loyalty_amount1;

            $net_amount = ($gross_amount) - ($discount_amount + $loyalty_amount1 + $coupon_amount);
        } else {
            $loyalty_amount1 = 0;
            $redeem_point = 0;
            $net_amount = ($gross_amount);
        }
        //$tax_amount = ($tax_percentage*($gross_amount - $discount_amount))/100;
        $tax_amount = ($tax_percentage * ($gross_amount)) / 100;


        $final_amount = $net_amount + $tax_amount;



        if ($final_amount == 0) {

            $order_status = 'returned';
        } else {

            $order_status = 'completed';
        }

        $final_return = $model_order['final_amount'] - $final_amount;

        if (!empty($model_customer)) {
            if ($model_loyalty['is_loyalty_on'] == '1') {
                $loyalty_amount = $model_loyalty['earned_amount'];

                $old_loyalty_points = round($model_order['final_amount'] / $loyalty_amount);



                $final_loyalty_points = round($final_amount / $loyalty_amount);
            } else {
                $loyalty_amount = 0;
                $old_loyalty_points = 0;
                $final_loyalty_points = 0;
            }



            $customer_loyalty_points = $customer_loyalty_points + $final_loyalty_points - $old_loyalty_points;



            $customer_loyalty_points = round($customer_loyalty_points);

            $model_customer->setAttribute('earned_loyalty_points', $customer_loyalty_points);

            $model_loyaltyPointsHistory->setAttribute('amount_spend', $final_amount);

            $model_loyaltyPointsHistory->setAttribute('earned_points', $final_loyalty_points);

            if (!empty($model_customerRedeem)) {

                $model_customerRedeem->setAttribute('redeem_points', $redeem_point); // Attribute added by Mohit for return product

                $model_customerRedeem->setAttribute('points_amount', $loyalty_amount1);

                $model_customerRedeem->save();
            }

            $model_loyaltyPointsHistory->save();

            $model_customer->save();
        }



        $model_order_new = array();

        $model_order_new = $model_order;

        $model_order_new->setAttribute('gross_amount', $gross_amount);

        $model_order_new->setAttribute('final_amount', $final_amount);

        $model_order_new->setAttribute('tax_amount', $tax_amount);

        $model_order_new->setAttribute('discount_percentage', $discount_percentage); // Attribute added by Mohit for return product

        $model_order_new->setAttribute('discount_amount', $discount_amount);

        $model_order_new->setAttribute('coupon_code', $coupon_code); // Attribute added by Mohit for return product

        $model_order_new->setAttribute('coupon_discount_amount', $coupon_amount);

        $model_order_new->setAttribute('return_amount', $final_return);

        $model_order_new->setAttribute('status', $order_status);

        if ($model_order_new->save()) {

            $model_order_new->id = sprintf("%012d", $model_order_new->id);

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
                'data' => array_filter($model_order_new->attributes),
                'return amount' => $final_return,
                'msg' => 'Order returned successfully'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        } else {

            $model_errors = $model->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
//                        'msg' => 'Category updated successfully',
                'errors' => $errors,
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//            $this->setHeader(400);
//            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);
        }
    }

    public function remove_arrays($errors) {

        $model_errors = $errors;

        foreach ($model_errors as $key => $value) {



            $errors[$key] = $value[0];
        }

        return [$errors];
    }

}
