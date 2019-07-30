<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class SalesChartController extends Controller

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

//        $access_token = Yii::$app->request->headers['access-token'];        
//
//        Yii::$app->common_functions->checkAccess($access_token);

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

        $query->from('orders')

              ->join('LEFT JOIN','users','users.id = orders.employee_id')

              ->where('orders.created_date between "'.$from_date.'" and "'.$to_date.'" and status="completed"')

              ->orWhere('orders.created_date="'.$date.'" and status="completed"')

              ->groupby('employee_id')

              ->orderby('sum(final_amount) DESC')

              ->limit('10')

              ->select('sum(final_amount) as total_sales,employee_id, users.first_name, users.last_name, count(*) as count');

        

        $command = $query->createCommand();

        $employee_array = $command->queryAll();

        $count = $query->count();

        

        $query = new Query;

        $query->from('orders')
                
              ->where('orders.created_date between "'.$from_date.'" and "'.$to_date.'" and status = "completed"')

              ->orWhere('orders.created_date="'.$date.'" and status = "completed"')

              ->select('sum(final_amount) as total_sales, count(*) as total_transactions');

        

        $command = $query->createCommand();

        $total_transactions = $command->queryOne();

        

        $i = 0;

        $newModel = array();

        foreach ($employee_array as $value) {

//            $newModel = $value;

            $user_name = ucfirst($value['first_name']).' '.ucfirst($value['last_name']);

            $newModel[$i]['employee_name'] = $user_name;

            $newModel[$i]['employee_total_sales'] = $value['total_sales'];

            $newModel[$i]['employee_no_of_transactions'] = $value['count'];

            $i++;

        }

        

        $data = [

            'employee_data' => $newModel,

            'total_transaction_data' => $total_transactions

        ];

        if(@$params['export'] == 'export')

        {
            $query = new Query;

            $query->from('orders')

                  ->join('LEFT JOIN','users','users.id = orders.employee_id')

                  ->where('orders.created_date between "'.$from_date.'" and "'.$to_date.'" and status="completed"')

                  ->orWhere('orders.created_date="'.$date.'" and status="completed"')

                  ->groupby('employee_id')

                  ->orderby('sum(final_amount) DESC')

                  ->select('sum(final_amount) as total_sales,employee_id, users.first_name, users.last_name, count(*) as count');



            $command = $query->createCommand();

            $employee_array = $command->queryAll();

            $count = $query->count();
            
            $i = 0;

            $exportModel = array();
            
            $exportModelDetails = array();
            
            $exportModelDetails['grand_total_sales'] = 0;
            
            $exportModelDetails['grand_total_transactions'] = 0;
            
            foreach ($employee_array as $value) {

//            $newModel = $value;

                $user_name = ucfirst($value['first_name']).' '.ucfirst($value['last_name']);

                $exportModel[$i]['employee_name'] = $user_name;

                $exportModel[$i]['employee_total_sales'] = $value['total_sales'];

                $exportModel[$i]['employee_no_of_transactions'] = $value['count'];
                
                $exportModelDetails['from_date'] = $from_date;
                
                $exportModelDetails['to_date'] = $to_date;
                
                $exportModelDetails['grand_total_sales'] = $exportModelDetails['grand_total_sales'] + $value['total_sales'];
                
                $exportModelDetails['grand_total_transactions'] = $exportModelDetails['grand_total_transactions'] + $value['count'];
                
                $i++;

            }

            $this->actionExcel($exportModel,$exportModelDetails);

        }

        else

        {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

                'data' =>$data,

    //                        'msg' => 'Customer updated successfully'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

    }

    

    public function actionExcel($array,$details)

    {

        $objPHPExcel = new \PHPExcel();

                

        $sheet=0;



        $objPHPExcel->setActiveSheetIndex($sheet);

        $row=5;

        foreach ($array as $foo) {

            $objPHPExcel->getActiveSheet()->getColumnDimension('A')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('B')->setWidth(20);

            $objPHPExcel->getActiveSheet()->getColumnDimension('C')->setWidth(20);



            $objPHPExcel->getActiveSheet()->setTitle($foo['employee_name'])

             ->setCellValue('A1', 'From Date')
             ->setCellValue('B1', $details['from_date'])
             ->setCellValue('A2', 'To Date')
             ->setCellValue('B2', $details['to_date'])
                    
             ->setCellValue('A4', 'Employee Name')

             ->setCellValue('B4', 'Employee Total Sales')

             ->setCellValue('C4', 'No. of Transactions');



                 



            $objPHPExcel->getActiveSheet()->setCellValue('A'.$row,$foo['employee_name']); 

            $objPHPExcel->getActiveSheet()->setCellValue('B'.$row,$foo['employee_total_sales']);

            $objPHPExcel->getActiveSheet()->setCellValue('C'.$row,$foo['employee_no_of_transactions']);

            $row++ ;

        }
        $row = $row+1;
        
        $objPHPExcel->getActiveSheet()->setCellValue('A'.$row,'Grand Total'); 
        $objPHPExcel->getActiveSheet()->setCellValue('B'.$row,$details['grand_total_sales']); 
        $objPHPExcel->getActiveSheet()->setCellValue('C'.$row,$details['grand_total_transactions']); 



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

