/*
pmenabler
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/
package org.eslack.pmenabler;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.content.Context;

public class MainActivity extends Activity {
	
	private TextView outputView;
	private Handler handler = new Handler();
	private Button buttonPm;
	
	private Context context;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		outputView = (TextView)findViewById(R.id.outputView);

		buttonPm = (Button)findViewById(R.id.buttonPm);
		buttonPm.setOnClickListener(onButtonPmClick);
		buttonPm.setClickable(false);

		String output;

		output("Disabled packages on your device:\n\n");
		output = exec("LD_LIBRARY_PATH=/system/lib pm list packages -d");
		output(output);
		if (output.length() == 0) {
			output("\nYou don't have any disabled packages.\n");
		} else {
			output("\nClick 'ENABLE' to enable them.\n");
		}
		buttonPm.setClickable(true);

	}

	private OnClickListener onButtonPmClick = new OnClickListener() {
		public void onClick(View v) {
			// disable button click if it has been clicked once
			buttonPm.setClickable(false);
			outputView.setText("");

			Thread thread = new Thread(new Runnable() {
				public void run() {

					String output;

					output("Enabling disabled packages (needs root)\n");
					output = exec("su -c 'export TIFS=$IFS ; export LD_LIBRARY_PATH=/system/lib/ ; P=`pm list packages -d` ; export IFS=: ; for p in $P ; do export IFS=$TIFS ; P2=`echo $p` ; for p2 in $P2 ; do pm enable $p2 ; done ; export IFS=: ; done ; export IFS=$TIFS'");
					output(output);

					output = exec("LD_LIBRARY_PATH=/system/lib pm list packages -d");
					output(output);
					if (output.length() == 0) {
						output("\nAll disabled packages have been enabled :)\n");
					} else {
						output("\nThere are still disabled packages, do you have root?\n");
					}
					buttonPm.setClickable(true);
				}
			});
			thread.start();
		}
	};


	private String exec(String command) {
	// execute a shell command, returning output in a string
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("sh");
			DataOutputStream os = new DataOutputStream(process.getOutputStream()); 
			os.writeBytes(command + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();

			BufferedReader reader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();

			process.waitFor();

			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


	private void output(final String str) {
		Runnable proc = new Runnable() {
			public void run() {
				if (str!=null) outputView.append(str);
			}
		};
		handler.post(proc);
	}

}
