<?php

namespace api\modules\v1\controllers;
 
use yii\rest\ActiveController; 
use Yii;
use api\modules\v1\models\User;
use api\modules\v1\models\Login;
use yii\data\ActiveDataProvider;
use yii\web\Controller;
use yii\web\NotFoundHttpException;
use yii\filters\VerbFilter;
//use yii\filters\auth\HttpBasicAuth;
use yii\db\Query;
use yii\db\ActiveRecord;
use yii\filters\AccessControl;
use yii\filters\auth\CompositeAuth;
use yii\filters\auth\HttpBasicAuth;
use yii\filters\auth\HttpBearerAuth;
use yii\filters\auth\QueryParamAuth;

class UserController extends Controller
{
    public $model = 'api\modules\v1\models\User';

    public function actionLogin()
    {
        $model = new LoginForm;

        if ($model->load(\Yii::$app->getRequest()->post()) && $model->signin()) {
            //return $this->goBack();
             echo \Yii::$app->user->identity->getAuthKey();
            //echo json_encode(['a'=>Yii::$app->user->getId()]);
        }

    }

    public function actionIndexx()
    {
        if (\Yii::$app->user->isGuest) {
            throw new \HttpHeaderException();
        }
        echo \Yii::$app->user->getId();
    }

}
