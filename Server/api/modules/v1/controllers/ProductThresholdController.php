<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class ProductThresholdController extends Controller

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

    * Lists all products whose quantity is less then minimum stock alert. 

    * @return mixed

    */  



    public function actionIndex()

    {     

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params = $_REQUEST;

        $query = new Query;

        $query->from('products')

              ->where('quantity <= min_stock_alert_qty')
                
              ->andWhere('mark_as_delete = 0')

              ->select("*");



        $command = $query->createCommand();

        $product_array = $command->queryAll();

        

        $img_path = "";

        $newModels = array();

        $i = 0;

        foreach ($product_array as $model)

        {

            $filename = $model['image'];

            $img_path = 'http://'.$_SERVER['HTTP_HOST'].Yii::getAlias('@api_url').('/product_images/').$filename;

            $newModels[] = $model;

            $newModels[$i]['image'] = $img_path;

            $i++; 

        }

        

        if(@$params['export'] == 'export')

        {

            $this->actionExcel($product_array);

        }

        

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$newModels,

//                        'msg' => 'Customer updated successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

 

    /**

    * Lists all products whose quantity is less then requested quantity. 

    * @return mixed

    */  



    public function actionView($id)

    {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $params = $_REQUEST;

//        print_r($params);exit;

        $query = new Query;

        $query->from('products')

              ->where('quantity <= '.$id)
                
              ->andWhere('mark_as_delete = 0')

              ->select("*");



        $command = $query->createCommand();

        $product_array = $command->queryAll();

        

        $img_path = "";

        $newModels = array();

        $i = 0;

        foreach ($product_array as $model)

        {

            $filename = $model['image'];

            $img_path = 'http://'.$_SERVER['HTTP_HOST'].Yii::getAlias('@api_url').('/product_images/').$filename;

            $newModels[] = $model;

            $newModels[$i]['image'] = $img_path;

            $i++; 

        }

        

        if(@$params['export'] == 'export')

        {

            $this->actionExcel($product_array);

        }



        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' =>$newModels,

//                        'msg' => 'Customer updated successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

 

    }

    

    public function actionExcel($product_array)

    {

        $objPHPExcel = new \PHPExcel();

                

        $sheet=0;



        $objPHPExcel->setActiveSheetIndex($sheet);

        $row=2;

        foreach ($product_array as $foo) {  

//            print_r($foo);exit;

            $objPHPExcel->getActiveSheet()->getColumnDimension('A')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('B')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('C')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('D')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('E')->setWidth(20);



            $objPHPExcel->getActiveSheet()->setTitle($foo['name'])



            ->setCellValue('A1', 'Name')

            ->setCellValue('B1', 'Quantity')

            ->setCellValue('C1', 'Selling Price')

            ->setCellValue('D1', 'Minimum Stock Alert Quantity')         

            ->setCellValue('E1', 'Barcode');



                 



            $objPHPExcel->getActiveSheet()->setCellValue('A'.$row,$foo['name']); 

            $objPHPExcel->getActiveSheet()->setCellValue('B'.$row,$foo['quantity']);

            $objPHPExcel->getActiveSheet()->setCellValue('C'.$row,$foo['selling_price']);

            $objPHPExcel->getActiveSheet()->setCellValue('D'.$row,$foo['min_stock_alert_qty']);

            $objPHPExcel->getActiveSheet()->setCellValue('E'.$row,$foo['barcode']);

            $row++ ;

        }



        header('Content-Type: application/vnd.ms-excel');



        $filename = "MyExcelReport_".date("d-m-Y-His").".csv";

        $img_path = Yii::getAlias('@api').'/upload_images/' . $filename;

        header('Content-Disposition: attachment;filename='.basename($img_path) .' ');

        header('Cache-Control: max-age=0');

        $objWriter = \PHPExcel_IOFactory::createWriter($objPHPExcel, 'Excel5');

        $objWriter->save('php://output');

        exit;

    }

}

