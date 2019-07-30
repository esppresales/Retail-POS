<?php

namespace api\modules\v1\controllers;

use Yii;
use api\modules\v1\models\Coupons;
use yii\web\Controller;
use yii\filters\VerbFilter;
use yii\db\Query;

class CouponsController extends Controller {

    public $model = 'api\modules\v1\models\Coupons';

    public function behaviors() {

        return [

            'verbs' => [

                'class' => VerbFilter::className(),
                'actions' => [

                    'index' => ['get'],
                    'view' => ['get'],
                    'create' => ['post'],
                    'update' => ['put'],
                    'delete' => ['delete'],
                    'deleteall' => ['post'],
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



            $this->setHeader(400);

            echo json_encode(array('status' => 0, 'error_code' => 400, 'message' => 'Method not allowed'), JSON_PRETTY_PRINT);

            exit;
        }



        return true;
    }

    /**

     * Lists all User models.

     * @return mixed

     */
    public function actionIndex() {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);



        $query = new Query;

        $query->from('coupons')

//                 ->join('INNER JOIN','user_info', 'user_info.user_id = registration.id')
                ->orderBy('id DESC')
                ->select("*");



        $command = $query->createCommand();

        $models = $command->queryAll();
        $j = 0;
        foreach ($models as $model) {

            $models[$j]['validity_from_date'] = date('d-M-Y', strtotime($model['validity_from_date']));
            $models[$j]['validity_to_date'] = date('d-M-Y', strtotime($model['validity_to_date']));
            $models[$j]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
            $models[$j]['modified_date'] = date('d-M-Y', strtotime($model['modified_date']));

            $j++;
        }

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,
            'data' => array_filter($models),
//                        'msg' => 'Customer created successfully'
        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
    }

    public function actionView($id) {

        $model = $this->findModel($id);



        $this->setHeader(200);

        echo json_encode(array('status' => 1, 'data' => array_filter($model->attributes)), JSON_PRETTY_PRINT);
    }

    /* function to find the requested record/model */

    protected function findModel($id) {

        $model = Coupons::findOne($id);

        if ($model !== null) {

            return $model;
        } else {



            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,
                //                        'data' =>array_filter($model->attributes),
                'msg' => 'Coupon Id not found'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

            exit;
        }
    }

    public function actionCreate() {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $params = $_REQUEST;

        $model = new Coupons();

        $model->attributes = $params;

        $date = date("Y-m-d");

        $model->setAttribute('created_date', $date);



        if ($model->save()) {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'msg' => 'Coupon added successfully'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        } else {

            $model_errors = $model->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'errors' => $errors,
//                        'msg' => 'Password does not match'
            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));
        }
    }

    public function actionUpdate($id) {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        Yii::$app->common_functions->_parsePut();

        $params = $GLOBALS['_PUT'];



        $model = $this->findModel($id);

        $model->attributes = $params;



        if ($model->save()) {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'msg' => 'Coupon updated successfully'
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

    public function actionDelete($id) {

        $access_token = Yii::$app->request->headers['access-token'];

        Yii::$app->common_functions->checkAccess($access_token);

        $model = $this->findModel($id);



        if ($model->delete()) {

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,
//                        'data' =>array_filter($model->attributes),
                'msg' => 'Coupon deleted successfully'
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

    public function remove_arrays($errors) {

        $model_errors = $errors;

        foreach ($model_errors as $key => $value) {



            $errors[$key] = $value[0];
        }

        return [$errors];
    }

}
