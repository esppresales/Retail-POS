<?php

/* File Description: Handles all the CRUD operations for Customer.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

use yii\rest\Controller;
use Yii;
use api\modules\v1\models\Discounts;
use api\modules\v1\models\Taxes;
use api\modules\v1\models\Orders;
use api\modules\v1\models\Coupons;
use api\modules\v1\models\LoyaltyPoints;
use api\modules\v1\models\ReceiptHeader;
use api\modules\v1\models\Currency;
use api\modules\v1\models\PeripheralConfiguration;
use api\modules\v1\models\PrinterConfiguration;
use yii\filters\VerbFilter;
use yii\db\Query;

class SettingsController extends Controller
{

  public function behaviors()
  {

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

   * Lists all Customer models.

   * @return mixed

   */
  public function actionIndex()
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

    $id = 1;



    $model_discount = Discounts::findOne($id);

    if (!empty($model_discount))
    {

      $model_discount = $model_discount->attributes;
    } else
    {

      $model_discount = null;
    }



    $model_tax = Taxes::findOne($id);

    if (!empty($model_tax))
    {

      $model_tax = $model_tax->attributes;
    } else
    {

      $model_tax = null;
    }



    $model_loyaltyPoints = LoyaltyPoints::findOne($id);

    if (!empty($model_loyaltyPoints))
    {

      $model_loyaltyPoints = $model_loyaltyPoints->attributes;
    } else
    {

      $model_loyaltyPoints = null;
    }



    $model_receiptHeader = ReceiptHeader::findOne($id);

    if (!empty($model_receiptHeader))
    {

      $logo_image = $model_receiptHeader->attributes['logo_image'];
      $logo_path = ($logo_image!='')?'http://'.$_SERVER['HTTP_HOST'].Yii::getAlias('@api_url').('/receipt_images/').$logo_image:'';

      $coupon_image = $model_receiptHeader->attributes['coupon_image'];
      $coupon_path = ($coupon_image!='')?'http://'.$_SERVER['HTTP_HOST'].Yii::getAlias('@api_url').('/receipt_images/').$coupon_image:'';

      $model_receiptHeader->setAttribute('logo_image', $logo_path);
      $model_receiptHeader->setAttribute('coupon_image', $coupon_path);

      $model_receiptHeader = $model_receiptHeader->attributes;
    } else
    {

      $model_receiptHeader = null;
    }



    $query = new Query;

    $query->from('currency')
            ->where('is_enabled = 1')
            ->select('*');

    $command = $query->createCommand();

    $model_currency = $command->queryOne();



    $model_peripheral = PeripheralConfiguration::findOne($id);

    if (!empty($model_peripheral))
    {

      $model_peripheral = $model_peripheral->attributes;
    } else
    {

      $model_peripheral = null;
    }


    $query = new Query;

    $query->from('printer_configuration')
            ->select('*');

    $command = $query->createCommand();

    $model_printer = $command->queryAll();


    $data = [

        'discount_details' => $model_discount,
        'tax_details' => $model_tax,
        'loyaltyPoint_details' => $model_loyaltyPoints,
        'printer_details' => $model_printer,
        'peripheral_details' => $model_peripheral,
        'receiptHeader_details' => $model_receiptHeader,
        'currency_details' => $model_currency
    ];



    $status = Yii::$app->common_functions->status('success_code');

    $response = [

        'status' => $status,
        'data' => $data,
            //                        'msg' => 'Customer created successfully'
    ];

    Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
  }

  /* function to find the requested record/model */

  protected function findModel($id)
  {

    $model = Coupons::findOne($id);

    if ($model !== null)
    {

      return $model;
    } else
    {

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

  /*   * * Function to add/edit settings ** */

  public function actionCreate()
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

    $params = $_REQUEST;
    
    $model_name = $params['model_name'];

    $model_name = ucfirst($model_name);

    $model_var = $model_name;

    $date = date('Y-m-d');

    if ($model_name == "Discounts")
    {

      $model = Discounts::findOne(1);

      if (empty($model))
      {

        $model = new Discounts();
      }

      $model_var = 'Discount updated successfully';
    }

    if ($model_name == "Taxes")
    {

      $model = Taxes::findOne(1);

      if (empty($model))
      {

        $model = new Taxes();
      }
      $model_var = 'Tax updated';
    }

    if ($model_name == "PeripheralConfiguration")
    {

      $model = PeripheralConfiguration::findOne(1);

      if (empty($model))
      {

        $model = new PeripheralConfiguration();
      }

      $model_var = "Peripheral configured successfully";
    }

    if ($model_name == "PrinterConfiguration")
    {
      $printer_type = $params['printer_type'];
      $printer_type = explode(',', $printer_type);
      $is_enabled = $params['is_enabled'];
      $is_enabled = explode(',', $is_enabled);
      $printer_name = $params['printer_name'];
      $printer_name = explode(',', $printer_name);
//            $count = count($printer_type);
      $i = 0;
      foreach ($printer_type as $type)
      {
        $query = new Query;

        $query->from('printer_configuration')
                ->where('printer_type = "' . $type . '"')
                ->select('id');

        $command = $query->createCommand();

        $model_printer = $command->queryOne();

        if (!empty($model_printer))
        {
          $printer_id = $model_printer['id'];
          $model = PrinterConfiguration::findOne($printer_id);
        } else
        {
          $model = new PrinterConfiguration();
        }
        $model->attributes = $params;
        $model->setAttribute('printer_type', $printer_type[$i]);
        $model->setAttribute('is_enabled', $is_enabled[$i]);
        $model->setAttribute('printer_name', $printer_name[$i]);
        $model->setAttribute('created_date', $date);
        $model->save();
        $model_var = "Printer Configuration added successfully";
        $i++;
//                $this->savePrinter($model,$model_var);
      }

      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
          'msg' => $model_var
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    }

    if ($model_name == "ReceiptHeader")
    {

      $model = ReceiptHeader::findOne(1);

      if (empty($model))
      {

        $model = new ReceiptHeader();
      }
      if (!empty($_FILES['logo_image']))
      {

        $data = file_get_contents($_FILES['logo_image']['tmp_name']);

        $fileName = time() . rand(1, 10000) . '.' . 'png';

        $upload_path = Yii::getAlias('@api') . '/receipt_images/';

        $model->setAttribute('logo_image', $fileName);

        file_put_contents($upload_path . $fileName, $data);
      }

      if (!empty($_FILES['coupon_image']))
      {

        $data = file_get_contents($_FILES['coupon_image']['tmp_name']);

        $fileName = time() . rand(1, 10000) . '.' . 'png';

        $upload_path = Yii::getAlias('@api') . '/receipt_images/';

        $model->setAttribute('coupon_image', $fileName);

        file_put_contents($upload_path . $fileName, $data);
      }

      $model_var = "Receipt Configuration added successfully";
    }

    if ($model_name == "Currency")
    {

      $id = $params['currency_id'];

      $model = Currency::findOne($id);

      $model->setAttribute('is_enabled', 1);



      $query = new Query;

      $query->createCommand()
              ->update('currency', ['is_enabled' => 0], 'id <> ' . $id)
              ->execute();

      $model_var = "Base currency updated";
    }

    if ($model_name == "LoyaltyPoints")
    {

      $model = LoyaltyPoints::findOne(1);

      if (empty($model))
      {

        $model = new LoyaltyPoints();
      }

      $model_var = "Loyalty points updated";
    }

    $model->attributes = $params;

    $model->setAttribute('created_date', $date);



    if ($model->save())
    {

      // Code added by Mohit Garg 19 Jan 2016
      // If Tax or discount is updated then update corresponding orders which are on hold 
      $model_orders = Orders::findAll([
                  'status' => "on hold",
      ]);
      if (!empty($model_orders))
      {
        foreach ($model_orders as $orders)
        {
          $gross_amount = $orders['gross_amount'];
          $final_amount = $orders['final_amount'];
          $discount_percentage = $orders['discount_percentage'];
          $discount_amount = $orders['discount_amount'];
          $final_without_discount_amount = $final_amount + $discount_amount;
          $tax_percentage = $orders['tax_percentage'];
          $tax_amount = $orders['tax_amount'];
          $final_without_tax_amount = $final_amount - $tax_amount;
          if ($model_name == "Discounts")
          {
            // Get discount amount
            $discount_percentage = $params['percentage'];
            $discount_amount = ($params['percentage'] * $gross_amount) / 100;
            $final_amount = $final_without_discount_amount - $discount_amount;
          }
          if ($model_name == "Taxes")
          {
            // Get Tax Amount
            $tax_percentage = $params['percentage'];
            $tax_amount = ($params['percentage'] * ($gross_amount)) / 100;
            $final_amount = $final_without_tax_amount + $tax_amount;
          }

          // Update the order table
          $orders->setAttribute('discount_percentage', $discount_percentage);
          $orders->setAttribute('discount_amount', $discount_amount);
          $orders->setAttribute('tax_percentage', $tax_percentage);
          $orders->setAttribute('tax_amount', $tax_amount);
          $orders->setAttribute('final_amount', $final_amount);
          $orders->save();
        }
      }
      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
          'msg' => $model_var
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

  function savePrinter($model, $model_var)
  {
    if ($model->save())
    {

      $status = Yii::$app->common_functions->status('success_code');

      $response = [

          'status' => $status,
          'msg' => $model_var
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

  public function actionDelete($id)
  {

    $access_token = Yii::$app->request->headers['access-token'];

    Yii::$app->common_functions->checkAccess($access_token);

    $model = PrinterConfiguration::findOne($id);

    if (count($model))
    {

      if ($model->delete())
      {

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,
//                        'data' =>array_filter($model->attributes),
            'msg' => 'Printer deleted successfully'
        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
      } else
      {

        $model_errors = $model->errors;

        $errors = $this->remove_arrays($model_errors);

        $status = Yii::$app->common_functions->status('error_code');

        $response = [

            'status' => $status,
//                        'data' =>array_filter($model->attributes),
            'errors' => [$errors],
//                        'msg' => 'Password does not match'
        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        exit;
      }
    } else
    {
      $status = Yii::$app->common_functions->status('error_code');

      $response = [

          'status' => $status,
          'msg' => 'Printer not found'
      ];

      Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
      exit;
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
