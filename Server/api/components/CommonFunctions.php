<?php
namespace api\components;
use Yii;
use yii\base\Component;
use yii\db\Query;

class CommonFunctions {
    
    public function status($status) {
        $codes = Array(
        'OK' => 200  ,
        'success_code' => 200,
        'error_code' => 400 ,
        'access_denied' => 403,
        'not_found' => 404
        );
        return (isset($codes[$status])) ? $codes[$status] : '';
    }
        /*
         * Function to send response
         * @author: Priyanka Agarwal
         */
     public function _sendResponse($status = 200, $body = '', $content_type = 'application/json') {
         $body = json_decode($body);
      
        $response = array();
       
        foreach($body as $key => $value)
        {
            if($key != 'status')
            {
                $response[$key] = $value;
            }
        }
       $body = json_encode($response);
      
        $status_header = 'HTTP/1.1 ' . $status . ' ' . $this->_getStatusCodeMessage($status);
        // set the status
        header($status_header);
        // set the content type
        header('Content-type: ' . $content_type);
        
        // pages with body are easy
        if ($body != '') {
            // send the body
            echo $body;
            exit;
        }
        // we need to create the body if none is passed
        else {
            // create some body messages
            $message = '';

            // this is purely optional, but makes the pages a little nicer to read
            // for your users.  Since you won't likely send a lot of different status codes,
            // this also shouldn't be too ponderous to maintain
            switch ($status) {
                case 400:
                    $message = 'Bad Request';
                    break;
                case 401:
                    $message = 'You must be authorized to view this page.';
                    break;
                case 403:
                    $message = 'You must be authorized to view this page.';
                    break;
                case 404:
                    $message = 'The requested URL ' . $_SERVER['REQUEST_URI'] . ' was not found.';
                    break;
                case 500:
                    $message = 'The server encountered an error processing your request.';
                    break;
                case 501:
                    $message = 'The requested method is not implemented.';
                    break;
            }

            // servers don't always have a signature turned on (this is an apache directive "ServerSignature On")
            $signature = ($_SERVER['SERVER_SIGNATURE'] == '') ? $_SERVER['SERVER_SOFTWARE'] . ' Server at ' . $_SERVER['SERVER_NAME'] . ' Port ' . $_SERVER['SERVER_PORT'] : $_SERVER['SERVER_SIGNATURE'];

            // this should be templatized in a real-world solution
            $body = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
                        <html>
                            <head>
                                <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
                                <title>' . $status . ' ' . $this->_getStatusCodeMessage($status) . '</title>
                            </head>
                            <body>
                                <h1>' . $this->_getStatusCodeMessage($status) . '</h1>
                                <p>' . $message . '</p>
                                <hr />
                                <address>' . $signature . '</address>
                            </body>
                        </html>';

            echo $body;
            exit;
        }
    }
    private function _getStatusCodeMessage($status)
    {
        $codes = Array(
        200 => 'OK',
        400 => 'Bad Request',
        401 => 'Unauthorized',
        402 => 'Payment Required',
        403 => 'Forbidden',
        404 => 'Not Found',
        500 => 'Internal Server Error',
        501 => 'Not Implemented',
        );
        return (isset($codes[$status])) ? $codes[$status] : '';
    }
    public function _parsePut() {
        global $_PUT;

        /* PUT data comes in on the stdin stream */
        $putdata = fopen("php://input", "r");

        /* Open a file for writing */
        // $fp = fopen("myputfile.ext", "w");

        $raw_data = '';

        /* Read the data 1 KB at a time
          and write to the file */
        while ($chunk = fread($putdata, 1024))
            $raw_data .= $chunk;

        /* Close the streams */
        fclose($putdata);

        // Fetch content and determine boundary
        $boundary = substr($raw_data, 0, strpos($raw_data, "\r\n"));

        if (empty($boundary)) {
            parse_str($raw_data, $data);
            $GLOBALS['_PUT'] = $data;
            return;
        }

        // Fetch each part
        $parts = array_slice(explode($boundary, $raw_data), 1);
        $data = array();

        foreach ($parts as $part) {
            // If this is the last part, break
            if ($part == "--\r\n")
                break;

            // Separate content from headers
            $part = ltrim($part, "\r\n");
            list($raw_headers, $body) = explode("\r\n\r\n", $part, 2);

            // Parse the headers list
            $raw_headers = explode("\r\n", $raw_headers);
            $headers = array();
            foreach ($raw_headers as $header) {
                list($name, $value) = explode(':', $header);
                $headers[strtolower($name)] = ltrim($value, ' ');
            }

            // Parse the Content-Disposition to get the field name, etc.
            if (isset($headers['content-disposition'])) {
                $filename = null;
                $tmp_name = null;
                preg_match(
                        '/^(.+); *name="([^"]+)"(; *filename="([^"]+)")?/', $headers['content-disposition'], $matches
                );
                list(, $type, $name) = $matches;

                //Parse File
                if (isset($matches[4])) {
                    //if labeled the same as previous, skip
                    if (isset($_FILES[$matches[2]])) {
                        continue;
                    }

                    //get filename
                    $filename = $matches[4];

                    //get tmp name
                    $filename_parts = pathinfo($filename);
//                print_r($filename_parts);exit;
                    $tmp_name = tempnam(ini_get('upload_tmp_dir'), $filename_parts['filename']);

                    //populate $_FILES with information, size may be off in multibyte situation
                    $_FILES[$matches[2]] = array(
                        'name' => $filename,
                        'type' => $value,
                        'tmp_name' => $tmp_name,
                        'error' => 0,
                        'size' => strlen($body)
                    );

                    //place in temporary directory
                    file_put_contents($tmp_name, $body);
                }
                //Parse Field
                else {
                    $data[$name] = substr($body, 0, strlen($body) - 2);
                }
            }
        }
        $GLOBALS['_PUT'] = $data;
        return;
    }
    
    public function checkAccess($access_token) {

        $query = new Query;
        $query->from('users')
              ->where('access_token="'.$access_token.'"')  
              ->select("*");
        $command = $query->createCommand();
        $models = $command->queryAll();
        $totalItems = $query->count();
        if($totalItems>0 && $access_token != "")
        {
            return true;
        }
        else
        {
            $status = $this->status('access_denied');
            $response = [
                'status' => $status,
//                        'data' => $newModels,
                'msg' => 'Your session has been expired. Please login again.'
            ];
            $this->_sendResponse($response['status'], json_encode($response));
        }
    }
// END OF CLASS
}
?>