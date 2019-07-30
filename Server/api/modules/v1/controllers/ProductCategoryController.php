<?php

/* File Description: Handles all the CRUD operations for Product Categories.

 * Author: Priyanka Agarwal

 */

namespace api\modules\v1\controllers;

 

use Yii;

use api\modules\v1\models\ProductCategories;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;



class ProductCategoryController extends Controller

{

    public $model = 'api\modules\v1\models\ProductCategories';

    

    public function behaviors()

    {

        return [

            'verbs' => [

                'class' => VerbFilter::className(),

                'actions' => [
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

    * Lists all Category models.

    * @return mixed

    */  


  /* function to find the requested record/model */

    protected function findModel($id)

    {

        $model = ProductCategories::findOne($id);

        if ($model !== null) {

        return $model;

        } else {

            $status = Yii::$app->common_functions->status('error_code');

                    $response = [

                        'status' => $status,

//                        'data' =>array_filter($model->attributes),

                        'msg' => 'Category Id not found'

                    ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        exit;

        }

    }

    /*
     * Added by Divya to delete the category from the database
     * 
     */

    public function actionDelete($id) {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $model = $this->findModel($id);
        if ($model->delete()) {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'msg' => 'Category deleted successfully'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        } else {

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
        }
    }    



}

