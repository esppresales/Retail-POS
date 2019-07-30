<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "receipt_header".
 *
 * @property integer $id
 * @property string $name
 * @property string $website
 * @property string $header1
 * @property string $header2
 * @property string $header3
 * @property string $created_date
 * @property string $modified_date
 */
class ReceiptHeader extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'receipt_header';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['created_date'], 'required'],
            [['created_date', 'modified_date'], 'safe'],
            [['name'], 'string', 'max' => 40],
            [['website'], 'string', 'max' => 100],
            [['header1', 'header2', 'header3'], 'string', 'max' => 50]
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'name' => 'Name',
            'website' => 'Website',
            'header1' => 'Header1',
            'header2' => 'Header2',
            'header3' => 'Header3',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
