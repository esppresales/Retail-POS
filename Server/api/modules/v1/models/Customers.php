<?php



namespace api\modules\v1\models;

use \yii\db\ActiveRecord;



/**

 * This is the model class for table "customers".

 *

 * @property integer $id

 * @property string $first_name

 * @property string $last_name

 * @property string $mobile_number

 * @property string $dob

 * @property string $gender

 * @property string $address

 * @property string $email_id

 * @property integer $earned_loyalty_points

 * @property string $membership_id

 * @property string $created_date

 * @property string $modified_date

 *

 * @property CustomerRedeemHistory[] $customerRedeemHistories

 * @property LoyaltyPointsHistory[] $loyaltyPointsHistories

 * @property Orders[] $orders

 */

class Customers extends ActiveRecord

{

    /**

     * @inheritdoc

     */

    public static function tableName()

    {

        return 'customers';

    }



    /**

     * @inheritdoc

     */

    public function rules()

    {

        return [

            [['first_name', 'mobile_number', 'membership_id'], 'required'],

            [['dob', 'created_date', 'modified_date'], 'safe'],

            [['gender', 'address','membership_type'], 'string'],

            [['earned_loyalty_points','membership_validity', 'country_id'], 'integer'],

            [['first_name', 'last_name', 'membership_id'], 'string', 'max' => 40],

            [['mobile_number','nric', 'postal_code'], 'string', 'max' => 20],

            [['email_id'], 'string', 'max' => 128],

            [['email_id', 'membership_id', 'nric', 'mobile_number'], 'unique']

        ];

    }



    /**

     * @inheritdoc

     */

    public function attributeLabels()

    {

        return [

            'id' => 'ID',

            'first_name' => 'First Name',

            'last_name' => 'Last Name',

            'mobile_number' => 'Mobile Number',

            'dob' => 'Dob',

            'gender' => 'Gender',

            'address' => 'Address',

            'email_id' => 'Email ID',

            'earned_loyalty_points' => 'Earned Loyalty Points',

            'membership_id' => 'Membership ID',

            'created_date' => 'Created Date',

            'modified_date' => 'Modified Date',
            // Added by Divya
            'nric' => 'NRIC',

        ];

    }



    /**

     * @return \yii\db\ActiveQuery

     */

    public function getCustomerRedeemHistories()

    {

        return $this->hasMany(CustomerRedeemHistory::className(), ['customer_id' => 'id']);

    }



    /**

     * @return \yii\db\ActiveQuery

     */

    public function getLoyaltyPointsHistories()

    {

        return $this->hasMany(LoyaltyPointsHistory::className(), ['customer_id' => 'id']);

    }



    /**

     * @return \yii\db\ActiveQuery

     */

    public function getOrders()

    {

        return $this->hasMany(Orders::className(), ['customer_id' => 'id']);

    }

    

    protected function findModel($id)

    {

        $model = Customers::findOne($id);

        if ($model !== null) {

        return $model;

        } else {

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

}

