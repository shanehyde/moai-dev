// Copyright (c) 2010-2011 Zipline Games, Inc. All Rights Reserved.
// http://getmoai.com

#include "pch.h"
#include <uslsext/uslsext.h>
#ifndef MOAI_OS_NACL
#include <openssl/conf.h>
#include <openssl/crypto.h>

#ifndef OPENSSL_NO_ENGINE
	#include <openssl/engine.h>
#endif

#ifndef OPENSSL_NO_ERR
	#include <openssl/err.h>
#endif

#include <openssl/ssl.h>
#endif

//----------------------------------------------------------------//
static void _cleanup () {

#ifndef MOAI_OS_NACL
	curl_global_cleanup ();
	
	#ifndef OPENSSL_NO_ENGINE
		ENGINE_cleanup ();
	#endif
	
	CONF_modules_unload ( 1 );
	
	#ifndef OPENSSL_NO_ERR
		ERR_free_strings ();
	#endif
	
	EVP_cleanup ();
	CRYPTO_cleanup_all_ex_data ();
#endif
}

//================================================================//
// uslsext
//================================================================//

//----------------------------------------------------------------//
void uslsext::InitGlobals ( USGlobals* globals ) {

	uslscore::InitGlobals ( globals );

	static bool sysInit = true;
	if ( sysInit ) {;

#ifndef MOAI_OS_NACL
		SSL_load_error_strings ();
		SSL_library_init (); 

		curl_global_init ( CURL_GLOBAL_WIN32 | CURL_GLOBAL_SSL );
#endif

		atexit ( _cleanup );
		sysInit = false;
	}

	USUrlMgr::Affirm ();
}
