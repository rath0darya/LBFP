<?php
declare(strict_types=1);
if(session_status()!==PHP_SESSION_ACTIVE){$https=(!empty($_SERVER['HTTPS'])&&$_SERVER['HTTPS']!=='off');session_name('LBFP_OWNER');session_set_cookie_params(['lifetime'=>0,'path'=>'/','secure'=>$https,'httponly'=>true,'samesite'=>'Strict']);ini_set('session.use_strict_mode','1');session_start();}
function owner_user():?array{return isset($_SESSION['owner_id'],$_SESSION['owner_username'])?['id'=>(int)$_SESSION['owner_id'],'username'=>(string)$_SESSION['owner_username']]:null;}
function owner_csrf():string{if(empty($_SESSION['owner_csrf']))$_SESSION['owner_csrf']=bin2hex(random_bytes(32));return (string)$_SESSION['owner_csrf'];}
function owner_require():void{if(!owner_user())reply(401,['ok'=>false,'message'=>'Please sign in again.']);}
function owner_check_csrf(array $d):void{$got=is_string($d['csrf']??null)?$d['csrf']:'';$want=(string)($_SESSION['owner_csrf']??'');if($want===''||!hash_equals($want,$got))reply(403,['ok'=>false,'message'=>'Security token expired. Refresh and try again.']);}
