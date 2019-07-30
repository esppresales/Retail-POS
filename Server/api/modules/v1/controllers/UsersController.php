<?php



/* File Description: Handles all the CRUD operations for User.

 * Author: Priyanka Agarwal

 */



namespace api\modules\v1\controllers;



use yii\rest\ActiveController;

use Yii;

use api\modules\v1\models\Users;

//use api\modules\v1\models\UserInfo;

use yii\data\ActiveDataProvider;

use yii\web\Controller;

use yii\filters\VerbFilter;

use yii\db\Query;

use yii\web\UploadedFile;



// include_once('class.stream.php');

class UsersController extends Controller {



    public $model = 'api\modules\v1\models\Users';



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

                    'deactivate' => ['post'],

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

        $query->from('users')
                
               ->where('username NOT LIKE "admin" ')
               ->andWhere(' access_token NOT LIKE "'.$access_token.'" OR access_token IS NULL')

               ->orderBy('id DESC')

               ->select("*");



        $command = $query->createCommand();

        $models = $command->queryAll();

        $img_path = "";

        $newModels = array();

        $i = 0;

        foreach ($models as $model) {

            $filename = $model['user_photo'];

            $img_path = 'http://'.$_SERVER['SERVER_NAME'] .Yii::getAlias('@api_url').('/upload_images/') . $filename;

            $newModels[] = $model;

            $newModels[$i]['user_photo'] = $img_path;

	    $newModels[$i]['dob'] = date('d-M-Y', strtotime($model['dob']));

            unset($newModels[$i]['password']);

            unset($newModels[$i]['password_hash']);

            unset($newModels[$i]['auth_key']);

            

            $query = new Query;

            $query->from('time_tracking')

                  ->where('user_id ='.$newModels[$i]['id'])

                  ->orderby('id DESC')

                  ->select("created_date, clock_in_time"); 

            $command = $query->createCommand();

            $time_tracking = $command->queryOne();

            $newModels[$i]['last_login'] = date('d-M-Y', strtotime($time_tracking['created_date']))." ".date('h:i A',  strtotime($time_tracking['clock_in_time']));



            $query = new Query;

            $query->from('orders')

                  ->where('employee_id ='.$newModels[$i]['id'])

                  ->select("id,final_amount,status,created_date"); 

            $command = $query->createCommand();

            $newModels[$i]['transactions'] = $command->queryAll();

		

	    $j = 0;

            foreach ($newModels[$i]['transactions'] as $model) {
                
		$newModels[$i]['transactions'][$j]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
                $newModels[$i]['transactions'][$j]['status'] = ucwords($model['status']);

		$j++;

	    }
            $i++;

        }

       

        $totalItems = $query->count();

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' => $newModels,

//                        'msg' => 'Customer created successfully'

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

    }



    public function actionView($id) {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        $img_path = "";

        $model = $this->findModel($id);

        $filename = $model->attributes['user_photo'];

        $query = new Query;



        $query->from('orders')

                ->where('employee_id='.$id)

              ->select("id,final_amount,status,created_date"); 

        $command = $query->createCommand();

        $transactions = $command->queryAll();

        if (!empty($filename)) {

            $img_path = 'http://'.$_SERVER['HTTP_HOST']  .Yii::getAlias('@api_url').('/upload_images/') . $filename;

        }

        $model->setAttribute('user_photo', $img_path);

        $model = $model->attributes;

        unset($model['password']);

        unset($model['password_hash']);

        $status = Yii::$app->common_functions->status('success_code');

        $response = [

            'status' => $status,

            'data' => array_filter($model),

            'transactions' => $transactions

        ];

        Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

    }



    /* function to find the requested record/model */



    protected function findModel($id) {

        $model = Users::findOne($id);

        if ($model !== null) {

            return $model;

        } else {

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,

//                        'data' =>array_filter($model->attributes),

                'msg' => 'User not found'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

            exit;

        }

    }



   

    public function actionCreate() {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

       

        $params = $_REQUEST;

        $model = new Users();

        $model->attributes = $params;

        $password = $model->attributes['password'];

        $dob = $model->attributes['dob'];

        $dob = date("Y-m-d", strtotime($dob));

        $model->setPassword($password);

//        $model->generateAuthKey();

        $date = date("Y-m-d");

        $model->setAttribute('created_date', $date);

        $model->setAttribute('dob', $dob);

        $fileName = '';

        if (!empty($_FILES)) {

            $data = file_get_contents($_FILES['user_photo']['tmp_name']);

            $fileName = time() . rand(1, 10000) . '.' . 'png';

            $upload_path = Yii::getAlias('@api') . '/upload_images/';

            $model->setAttribute('user_photo', $fileName);

        }

        if ($model->save()) {

            if (!empty($_FILES)) {

                file_put_contents($upload_path . $fileName, $data);

            }

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

//                'data' => array_filter($model->attributes),

                'msg' => 'Employee added Successfully'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        } else {

            $model_errors = $model->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,

                'errors' => $errors,

//                        'data' =>array_filter($model->attributes),

//                        'msg' => 'Customer not found'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

//            $this->setHeader(400);

//            echo json_encode(array('status'=>0,'error_code'=>400,'errors'=>$model->errors),JSON_PRETTY_PRINT);

        }

    }

    

    public function actionUpdate($id) {
        
        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        Yii::$app->common_functions->_parsePut();

        $params = $GLOBALS['_PUT'];

        $model = $this->findModel($id);

        $old_file = $model->attributes['user_photo'];

        $old_pwd = $model->attributes['password'];
        
        $access_token_user = $model->attributes['access_token'];

        $model->attributes=$params;

        if($model->attributes['password'] == "")

        {

            $model->setAttribute('password', $old_pwd);

        }

        else

        {

            $model->setPassword($model->attributes['password']);
            if($access_token != $access_token_user)
            {
                $model->setAttribute('access_token', " ");
            }

        }
        
        if($model->attributes['dob'] != "")

        {
        
            $dob = $model->attributes['dob'];

            $dob = date("Y-m-d", strtotime($dob));
            
            $model->setAttribute('dob', $dob);
        
        
        }

        $fileName = '';

        if (!empty($model->attributes['user_photo'])) {

            $pic=$model->attributes['user_photo'];

            $data = base64_decode($pic);

            $fileName = time() . rand(1, 10000) . '.' . 'png';

            $upload_path = Yii::getAlias('@api') . '/upload_images/';

            $model->setAttribute('user_photo', $fileName);            

        } 
        else 
        {
          $model->setAttribute('user_photo', $old_file);
        }


        if ($model->save()) {

            

            if ($model->attributes['user_photo'] != $old_file) {

                file_put_contents($upload_path . $fileName, $data);

                unlink(Yii::getAlias('@api') . '/upload_images/' . $old_file);

            }

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

                'msg' => 'User profile updated successfully'

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        } else {

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



    public function actionDeactivate($id) {

        $access_token = Yii::$app->request->headers['access-token'];        

        Yii::$app->common_functions->checkAccess($access_token);

        

        $params = $_REQUEST;

        $fileName = '';

        $model1 = $this->findModel($id);

        $model1->attributes = $params;

        $is_active = $model1->attributes['is_active'];
        $access_token_user = "";

        if ($is_active == 1) {

            $is_active = 0;
            $var = 'deactivated';

        } else if ($is_active == 0) {

            $is_active = 1;
            $var = 'activated';

        }

        $model1->setAttribute('is_active', $is_active);
        $model1->setAttribute('access_token', $access_token_user);

        

        

        if ($model1->save()) {

            

            $query = new Query;

            $query->from('users')
                  ->where('username NOT LIKE "admin" ')
                  ->andWhere('access_token NOT LIKE "'.$access_token.'" OR access_token IS NULL')
                  ->orderBy('id DESC')
                  ->select("*");



            $command = $query->createCommand();

            $models = $command->queryAll();

            $img_path = "";

            $newModels = array();

            $i = 0;

            foreach ($models as $model) {

                $filename = $model['user_photo'];

                $img_path = 'http://'.$_SERVER['HTTP_HOST'] .Yii::getAlias('@api_url').('/upload_images/') . $filename;

                $newModels[] = $model;

                $newModels[$i]['user_photo'] = $img_path;

	        $newModels[$i]['dob'] = date('d-M-Y', strtotime($model['dob']));

                unset($newModels[$i]['password']);

                unset($newModels[$i]['password_hash']);

                

                $query = new Query;

                $query->from('time_tracking')

                      ->where('user_id ='.$newModels[$i]['id'])

                      ->orderby('id DESC')

                      ->select("created_date, clock_in_time"); 

                $command = $query->createCommand();

                $time_tracking = $command->queryOne();

                $newModels[$i]['last_login'] = date('d-M-Y', strtotime($time_tracking['created_date']))." ".date('h:i A',  strtotime($time_tracking['clock_in_time']));



                

                $query = new Query;

                $query->from('orders')

                      ->where('employee_id ='.$newModels[$i]['id'])

                      ->select("id,final_amount,status,created_date"); 

                $command = $query->createCommand();

                $newModels[$i]['transactions'] = $command->queryAll();	

		$j = 0;

            	foreach ($newModels[$i]['transactions'] as $model) {



			$newModels[$i]['transactions'][$j]['created_date'] = date('d-M-Y', strtotime($model['created_date']));
                        $newModels[$i]['transactions'][$j]['status'] = ucwords($model['status']);

			$j++;

	    	}

                $i++;

            }

            $status = Yii::$app->common_functions->status('success_code');

            $response = [

                'status' => $status,

                'data' =>$newModels,

                'msg' => 'User ' . $var,

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        } else {

            $model_errors = $model1->errors;

            $errors = $this->remove_arrays($model_errors);

            $status = Yii::$app->common_functions->status('error_code');

            $response = [

                'status' => $status,

                'errors' => $errors,

            ];

            Yii::$app->common_functions->_sendResponse($response['status'], json_encode($response));

        }

    }

    public function remove_arrays($errors)

    {

        $model_errors = $errors;

        foreach ($model_errors as  $key=>$value) {



            $errors[$key] = $value[0];



        }

        return [$errors];

    }



}

