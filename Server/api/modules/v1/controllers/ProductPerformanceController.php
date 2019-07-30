<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use yii\web\Controller;
use api\modules\v1\models\Currency;
use yii\filters\VerbFilter;

use yii\db\Query;



class ProductPerformanceController extends Controller

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

    * Return Sales of top-10 employees.

    * @return mixed

    */  



    public function actionIndex()

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params = $_REQUEST;

        $date = date('Y-m-d');

        $from_date = null;

        $to_date = null;

        if(@$params['from_date'] != null)

        {

            $from_date = $params['from_date'];

            $from_date = date('Y-m-d',  strtotime($from_date));

        }

        if(@$params['to_date'] != null)

        {

            $to_date = $params['to_date'];

            $to_date = date('Y-m-d',  strtotime($to_date));

        }

        

        $query = new Query;

        $query->from('order_items')

              ->join('LEFT JOIN','products','products.id = order_items.product_id')                
              ->join('LEFT JOIN','orders','orders.id = order_items.order_id')
              ->Where('order_items.created_date between "'.$from_date.'" and "'.$to_date.'" and products.mark_as_delete = 0 AND orders.status="completed"')

              ->orWhere('order_items.created_date="'.$date.'" and products.mark_as_delete = 0 AND orders.status="completed"')
                
//              ->andWhere('products.mark_as_delete = 0')

              ->groupby('product_id')

              ->orderby('sum(qty - return_qty) DESC')

              ->limit('10')

              ->select('sum(qty - return_qty) as total_quantity, products.name, count(*) as count, products.id, order_items.product_price');

        

        $command = $query->createCommand();

        $product_array = $command->queryAll();


        
        $query = new Query;

        $query->from('order_items')
              ->join('LEFT JOIN','orders','orders.id = order_items.order_id')
              ->Where('order_items.created_date between "'.$from_date.'" and "'.$to_date.'" AND orders.status="completed"')

              ->orWhere('order_items.created_date="'.$date.'" AND orders.status="completed"')

              ->select('sum(qty - return_qty) as total_quantity');

        

        $command = $query->createCommand();

        $total_items_sold = $command->queryOne();

        

        $i = 0;

        $top_product = 0;

        $total_products = 0;

        $product_performance = array();

        foreach ($product_array as $value) {

//            $newModel = $value;

            if($value['total_quantity'] > $top_product)

            {

                $top_product = $value['total_quantity'];

                $top_product_id = $value['id'];

                $top_product_name = $value['name'];

                $top_product_price = $value['product_price'];

            }

            $total_products = $value['total_quantity'] + $total_products;

            $product_performance[$i]['product_name'] = $value['name'];

            $product_performance[$i]['quantity_sold'] = $value['total_quantity'];

            $i++;

        }

        // Code added by Mohit Garg for adding currency on the end of top product price
        $model_currency = Currency::findOne([
                        'is_enabled' => '1'
            ]);

        $top_product_details = [

            'total_items_sold' => $total_products,

            'top_selling_product' => $top_product_name,

            'top_product_price' => $model_currency['short_name'].' '.$top_product_price

        ];

        

        

        $data = [

            'product_performance' => $product_performance,

            'top_product_details' => $top_product_details

        ];

        

        if(@$params['export'] == 'export')

        {

            $this->actionExcel($product_performance);

        }

        else

        {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

                'data' => $data,

    //                        'msg' => 'Customer updated successfully'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

    }

    

    public function actionExcel($array)

    {

        $objPHPExcel = new \PHPExcel();

                

        $sheet=0;



        $objPHPExcel->setActiveSheetIndex($sheet);

        $row=2;

        foreach ($array as $foo) {  

//            print_r($foo);exit;

            $objPHPExcel->getActiveSheet()->getColumnDimension('A')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('B')->setWidth(20);



            $objPHPExcel->getActiveSheet()->setTitle($foo['product_name'])



             ->setCellValue('A1', 'Product Name')

             ->setCellValue('B1', 'Quantity Sold');



                 



            $objPHPExcel->getActiveSheet()->setCellValue('A'.$row,$foo['product_name']); 

            $objPHPExcel->getActiveSheet()->setCellValue('B'.$row,$foo['quantity_sold']);

            $row++ ;

        }



        header('Content-Type: application/vnd.ms-excel');



        $filename = "MyExcelReport_".date("d-m-Y-His").".xls";

        $img_path = Yii::getAlias('@api').'/upload_images/' . $filename;

        header('Content-Disposition: attachment;filename='.basename($img_path) .' ');

        header('Cache-Control: max-age=0');

        $objWriter = \PHPExcel_IOFactory::createWriter($objPHPExcel, 'Excel5');

        $objWriter->save('php://output');

//        $objWriter->save($img_path);

        exit;

    }

    

    



}

