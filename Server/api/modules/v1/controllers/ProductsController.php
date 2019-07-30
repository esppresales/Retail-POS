<?php

/* File Description: Handles all the CRUD operations for Products.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

use Yii;
use api\modules\v1\models\Products;
use api\modules\v1\models\Orders;
use api\modules\v1\models\OrderItems;
use yii\web\Controller;
use yii\filters\VerbFilter;
use yii\db\Query;
use yii\helpers\Url;

class ProductsController extends Controller
{

  public $model = 'api\modules\v1\models\Products';

  public function behaviors()
  {

    return [

        'verbs' => [

            'class' => VerbFilter::className(),
            'actions' => [

                'index' => ['get'],
                'view' => ['get'],
                'create' => ['post'],
                'update' => ['put'],
                'delete' => ['delete'],
            ],
        ],
    ];
  }

  public function beforeAction($event)
  {

    $action = $event->id;

    if (isset($this->actions[$action]))
    {

      $verbs = $this->actions[$action];
    } elseif (isset($this->actions['*']))
    {

      $verbs = $this->actions['*'];
    } else
    {

      return $event->isValid;
    }

    $verb = Yii::$app->getRequest()->getMethod();



    $allowed = array_map('strtoupper', $verbs);



    if (!in_array($verb, $allowed))
    {



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

  /**

   * Lists all Product models.

   * @return mixed

   */
  public function actionIndex()
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);



    $query = new Query;

    $query->from('products')
            ->where('mark_as_delete = 0')
            ->orderBy('id DESC')
            ->select("*");



    $command = $query->createCommand();

    $models = $command->queryAll();

    $img_path = "";

    $newModels = array();

    $i = 0;

    foreach ($models as $model)
    {

      $filename = $model['image'];

      $img_path = 'http://' . $_SERVER['HTTP_HOST'] . Yii::getAlias('@api_url') . ('/product_images/') . $filename;

      $newModels[] = $model;

      $newModels[$i]['image'] = $img_path;

      $i++;
    }



    $totalItems = $query->count();

    $status = Yii::$app->common_functions->status('success_code');

    $response = [

        'status' => $status,
        'data' => $newModels,
        'totalItems' => $totalItems,
    ];

    Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
  }

  /*   * * Function to view the details of a particular product ** */

  public function actionView($id)
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);



    $img_path = "";



    $model = $this->findModel($id);



    $filename = $model->attributes['image'];

    if (!empty($filename))
    {

      $img_path = 'http://' . $_SERVER['HTTP_HOST'] . Yii::getAlias('@api_url') . ('/product_images/') . $filename;
    }

    $model->setAttribute('image', $img_path);



    $status = Yii::$app->common_functions->status('success_code');



    $response = [

        'status' => $status,
        'data' => array_filter($model->attributes),
//                        'imagePath' => $img_path,
//                        'msg' => 'Customer updated successfully'
    ];

    Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
  }

  /* function to find the requested record/model */

  protected function findModel($id)
  {

    $model = Products::findOne($id);

    if ($model !== null)
    {

      return $model;
    } else
    {

      $status = Yii::$app->common_functions->status('error_code');

      $response = [

          'status' => $status,
          //                        'data' =>array_filter($model->attributes),
          'msg' => 'Id not found'
      ];



      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//        $this->setHeader(400);
//        echo json_encode(array('status'=>0,'error_code'=>400,'message'=>'Bad request'),JSON_PRETTY_PRINT);

      exit;
    }
  }

  /*   * * Function to add the new product ** */

  public function actionCreate()
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

    $params = $_REQUEST;

    $model = new Products();

    $model->attributes = $params;

    $fileName = '';

    $date = date("Y-m-d");

    $model->setAttribute('created_date', $date);

    $fileName = '';

    if (!empty($_FILES))
    {

      $data = file_get_contents($_FILES['image']['tmp_name']);

      $fileName = time() . rand(1, 10000) . '.' . 'png';

      $upload_path = Yii::getAlias('@api') . '/product_images/';

      $model->setAttribute('image', $fileName);
    }

    $category = $model->getCategory();

//        print_r($category);exit;

    if ($model->save())
    {

      if (!empty($_FILES))
      {

        file_put_contents($upload_path . $fileName, $data);
      }

      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
//                        'data' =>array_filter($model->attributes),
          'msg' => 'Product added successfully'
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    } else
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

  /*   * * Function to update the details of a particular product ** */

  public function actionUpdate($id)
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

//        $params=$_REQUEST;
//        $params= Yii::$app->request->getBodyParams();

    Yii::$app->common_functions->_parsePut();

    $params = $GLOBALS['_PUT'];

    $model = $this->findModel($id);

    $old_selling_price = $model->attributes['selling_price'];
    $old_file = $model->attributes['image'];

    $model->attributes = $params;

//        print_r($params);
//        print_r($_FILES);
//        exit;

    $fileName = '';



    if (!empty($model->attributes['image']))
    {

      $pic = $model->attributes['image'];

      $data = base64_decode($pic);

      $fileName = time() . rand(1, 10000) . '.' . 'png';

      $upload_path = Yii::getAlias('@api') . '/product_images/';

      $model->setAttribute('image', $fileName);
    }
    else 
    {
      $model->setAttribute('image', $old_file);
    }

    if ($model->save())
    {

      if ($model->attributes['image'] != $old_file)
      {

        file_put_contents($upload_path . $fileName, $data);

        unlink(Yii::getAlias('@api') . '/product_images/' . $old_file);
      }

      // Code added by Mohit Garg 19 Jan 2016
      // If product's selling price is updated then update corresponding orders 
      // Check if selling price of product is updated or not  
      if ($model->attributes['selling_price'] != $old_selling_price)
      {
        $model_orders = Orders::findAll([
                    'status' => "on hold",
        ]);
        if (!empty($model_orders))
        {
          foreach ($model_orders as $orders)
          {
            // Check is this product exist in this order
            $model_orderItem = OrderItems::findOne([
                        'order_id' => $orders['id'],
                        'product_id' => $id,
            ]);
            if (!empty($model_orderItem))
            {

              // Update the price in order_items table

              $model_orderItem->setAttribute('product_price', $model->attributes['selling_price']);

              $model_orderItem->save();

              //Get loyalty point of order
              $loyalty = ($orders['gross_amount'] + $orders['tax_amount']) - ($orders['final_amount'] + $orders['discount_amount'] + $orders['coupon_discount_amount']);

              // Get Gross Amount of order
              $gross_amount = 0;
              $model_orderItems = OrderItems::findAll([
                          'order_id' => $orders['id']
              ]);
              foreach ($model_orderItems as $orderitem)
              {
                $gross_amount = ($orderitem['qty'] * $orderitem['product_price']) + $gross_amount;
              }
              // Get discount amount
              $discount_amount = ($orders['discount_percentage'] * $gross_amount) / 100;
              // Get Tax Amount
              $tax_amount = ($orders['tax_percentage'] * ($gross_amount)) / 100;
              // Final Amount
              $final_amount = ($gross_amount + $tax_amount) - ($orders['coupon_discount_amount'] + $loyalty + $discount_amount);

              // Update the order table
              $orders->setAttribute('gross_amount', $gross_amount);
              $orders->setAttribute('discount_amount', $discount_amount);
              $orders->setAttribute('tax_amount', $tax_amount);
              $orders->setAttribute('final_amount', $final_amount);
              $orders->save();
            }
          }
        }
      }
      // Code Ends by Mohit Garg
      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
//                        'data' =>array_filter($model->attributes),
          'msg' => 'Product updated successfully'
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    } else
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

  /*   * * Function to delete the particular product ** */

  public function actionDelete($id)
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

    $model = $this->findModel($id);

    $filename = $model->attributes['image'];

    $model->setAttribute('mark_as_delete', 1);



    if ($model->save(false))
    {
      
      // Code added by Mohit Garg 20 Jan 2016
        $model_orders = Orders::findAll([
                    'status' => "on hold",
        ]);
        if (!empty($model_orders))
        {
          foreach ($model_orders as $orders)
          {
            // Check is this product exist in this order
            $model_orderItem = OrderItems::findOne([
                        'order_id' => $orders['id'],
                        'product_id' => $id,
            ]);
            if (!empty($model_orderItem))
            {

              // delete the product in order_items table
              $model_orderItem->delete();

              //Get loyalty point of order
              $loyalty = ($orders['gross_amount'] + $orders['tax_amount']) - ($orders['final_amount'] + $orders['discount_amount'] + $orders['coupon_discount_amount']);

              // Get Gross Amount of order
              $gross_amount = 0;
              $model_orderItems = OrderItems::findAll([
                          'order_id' => $orders['id']
              ]);
              foreach ($model_orderItems as $orderitem)
              {
                $gross_amount = ($orderitem['qty'] * $orderitem['product_price']) + $gross_amount;
              }
              // Get discount amount
              $discount_amount = ($orders['discount_percentage'] * $gross_amount) / 100;
              // Get Tax Amount
              $tax_amount = ($orders['tax_percentage'] * ($gross_amount)) / 100;
              // Final Amount
              $final_amount = ($gross_amount + $tax_amount) - ($orders['coupon_discount_amount'] + $loyalty + $discount_amount);

              // Update the order table
              $orders->setAttribute('gross_amount', $gross_amount);
              $orders->setAttribute('discount_amount', $discount_amount);
              $orders->setAttribute('tax_amount', $tax_amount);
              $orders->setAttribute('final_amount', $final_amount);
              $orders->save();
            }
          }
        }
      // Code Ends by Mohit Garg

//            if(!empty($filename))
//            {
//                unlink(Yii::getAlias('@api') . '/product_images/'.$filename);
//            }

      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
//                        'data' =>array_filter($model->attributes),
          'msg' => 'Product deleted successfully'
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    } else
    {

      $model_errors = $model->errors;

      $errors = $this->remove_arrays($model_errors);

      $status = Yii::$app->common_functions->status('error_code');

      $response = [

          'status' => $status,
          'errors' => $errors,
//                        'data' =>array_filter($model->attributes),
//                        'msg' => 'Customer updated successfully'
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    }
  }

  public function remove_arrays($errors)
  {

    $model_errors = $errors;

    foreach ($model_errors as $key => $value)
    {



      $errors[$key] = $value[0];
    }

    return [$errors];
  }

}
