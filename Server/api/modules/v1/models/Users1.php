<?php

namespace api\modules\v1\models;
use \yii\db\ActiveRecord;

/**
 * This is the model class for table "users".
 *
 * @property integer $id
 * @property string $first_name
 * @property string $last_name
 * @property string $email_id
 * @property string $password
 * @property integer $role_id
 * @property string $mobile_number
 * @property string $dob
 * @property string $gender
 * @property integer $is_active
 * @property string $created_date
 * @property string $modified_date
 * @property string $user_photo
 *
 * @property Orders[] $orders
 * @property Products[] $products
 * @property TimeTracking[] $timeTrackings
 * @property Roles $role
 */
class Users extends ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'users';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['first_name', 'last_name', 'email_id', 'password', 'role_id', 'mobile_number', 'dob', 'gender', 'is_active', 'created_date', 'user_photo'], 'required'],
            [['role_id', 'is_active'], 'integer'],
            [['dob', 'created_date', 'modified_date'], 'safe'],
            [['gender'], 'string'],
            [['first_name', 'last_name', 'password'], 'string', 'max' => 40],
            [['email_id'], 'string', 'max' => 128],
            [['mobile_number'], 'string', 'max' => 20],
            [['user_photo'], 'string', 'max' => 100],
            [['email_id'], 'unique']
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
            'email_id' => 'Email ID',
            'password' => 'Password',
            'role_id' => 'Role ID',
            'mobile_number' => 'Mobile Number',
            'dob' => 'Dob',
            'gender' => 'Gender',
            'is_active' => 'Is Active',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
            'user_photo' => 'User Photo',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getOrders()
    {
        return $this->hasMany(Orders::className(), ['employee_id' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getProducts()
    {
        return $this->hasMany(Products::className(), ['added_by' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getTimeTrackings()
    {
        return $this->hasMany(TimeTracking::className(), ['user_id' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getRole()
    {
        return $this->hasOne(Roles::className(), ['id' => 'role_id']);
    }
}
