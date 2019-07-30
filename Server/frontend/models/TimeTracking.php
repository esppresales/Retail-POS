<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "time_tracking".
 *
 * @property integer $id
 * @property integer $user_id
 * @property string $clock_in_time
 * @property string $clock_out_time
 * @property string $created_date
 * @property string $modified_date
 *
 * @property Users $user
 */
class TimeTracking extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'time_tracking';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['user_id', 'clock_in_time', 'clock_out_time', 'created_date'], 'required'],
            [['user_id'], 'integer'],
            [['clock_in_time', 'clock_out_time', 'created_date', 'modified_date'], 'safe']
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'user_id' => 'User ID',
            'clock_in_time' => 'Clock In Time',
            'clock_out_time' => 'Clock Out Time',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getUser()
    {
        return $this->hasOne(Users::className(), ['id' => 'user_id']);
    }
}
