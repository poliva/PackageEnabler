/*
pmenabler
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/
package org.eslack.pmenabler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartupIntentReceiver extends BroadcastReceiver{
	Utils utils = new Utils();
	@Override
	public void onReceive(Context context, Intent intent) {
		
		utils.exec("su -c 'export TIFS=$IFS ; export LD_LIBRARY_PATH=/system/lib/ ; P=`pm list packages -d` ; export IFS=: ; for p in $P ; do export IFS=$TIFS ; P2=`echo $p` ; for p2 in $P2 ; do pm enable $p2 ; done ; export IFS=: ; done ; export IFS=$TIFS'");

	}

}
