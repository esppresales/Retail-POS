<?php



namespace api\modules\v1\models;

use \yii\db\ActiveRecord;



/**

 * This is the model class for table "printer_configuration".

 *

 * @property integer $id

 * @property string $printer_type

 * @property string $is_enabled
 
 * @property string $printer_name 
 

 * @property string $created_date

 * @property string $modified_date

 */

class PrinterConfiguration extends ActiveRecord

{

    /**

     * @inheritdoc

     */

    public static function tableName()

    {

        return 'printer_configuration';

    }



    /**

     * @inheritdoc

     */

    public function rules()

    {

        return [

            [['created_date', 'printer_type', 'printer_name'], 'required'],

            [['created_date', 'modified_date'], 'safe'],

            [['printer_type','printer_name'], 'string', 'max' => 40],
            
            [['is_enabled'], 'number'],

        ];

    }



    /**

     * @inheritdoc

     */

    public function attributeLabels()

    {

        return [

            'id' => 'ID',

            'printer_type' => 'Printer Type',

            'is_enabled' => 'Is Enabled',

            'created_date' => 'Created Date',

            'modified_date' => 'Modified Date',

        ];

    }

}

