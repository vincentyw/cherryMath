package com.example.mathlab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ViewFlipper;
import android.graphics.Color;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MenuItem;
import android.widget.Spinner;
import android.view.KeyEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Date;

import android.database.*;
import android.media.*;
import android.text.method.ScrollingMovementMethod;


public class MyTab extends Activity implements OnGestureListener {
	private ViewFlipper viewFlipper;
	private GestureDetector detector;
	Animation leftInAnimation;
	Animation leftOutAnimation;
	Animation rightInAnimation;
	Animation rightOutAnimation;

	private MathSettings settings = new MathSettings();
	private MathItem mathItems = new MathItem();
	private int count = 0;
	private int num_answer, num_click, num_error = 0;
	private boolean num_started = false;
	private long startTime = 0;
	private MediaPlayer  player1;  
	private MediaPlayer  player2;  
	private MediaPlayer  player3;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
		detector = new GestureDetector(this);

		final Spinner s1 = (Spinner)findViewById(R.id.spin_numberRange);
		final Spinner s2 = (Spinner)findViewById(R.id.spin_questionAmount);
		final Spinner s3 = (Spinner)findViewById(R.id.spin_questionType);
		final Spinner s4 = (Spinner)findViewById(R.id.spin_handleMethod);

		player1 = MediaPlayer.create(this,R.raw.error);
		player2 = MediaPlayer.create(this,R.raw.right);
		player3 = MediaPlayer.create(this,R.raw.great);
		
		MathTable table = queryData();

		((TextView)findViewById(R.id.textView_historyStats)).setText(
				getText(R.string.str12)+
				String.valueOf(table.total)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				String.valueOf(table.correct)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				String.valueOf(table.totaltime)+
				getText(R.string.str16));				
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.range_array,android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter1);
		s1.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						settings.range = Integer.valueOf(s1.getSelectedItem().toString()).intValue();
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				}
				);

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.amount_array,android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter2);
		s2.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						settings.amount = Integer.valueOf(s2.getSelectedItem().toString()).intValue();
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				}
				);

		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.function_array,android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s3.setAdapter(adapter3);
		s3.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						switch ( s3.getSelectedItemPosition() )
						{
						case 0:
							settings.type = 1;
							break;
						case 1:
							settings.type = 2;
							break;
						case 2:
							settings.type = 3;
							break;
						case 3:
							settings.type = 4;
							break;
						default:
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				}
				);

		ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.mode_array,android.R.layout.simple_spinner_item);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s4.setAdapter(adapter4);
		s4.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						if (0 == s4.getSelectedItemPosition())
						{
							settings.mode = false;
						}
						else
						{
							settings.mode = true;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				}
				);

		final TextView tv5 = (TextView)findViewById(R.id.textView_answer);
		final TextView tv6 = (TextView)findViewById(R.id.textView_status);
		final Button btn_answer = (Button)findViewById(R.id.button_answer);
		final Button btn_clear = (Button)findViewById(R.id.button_clear);

		Button b1 = (Button)findViewById(R.id.button_start);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MathTable table = queryData();

				((TextView)findViewById(R.id.textView_historyStats)).setText(
						getText(R.string.str12)+
						String.valueOf(table.total)+
						getText(R.string.str13)+
						"\n"+
						getText(R.string.str14)+
						String.valueOf(table.correct)+
						getText(R.string.str13)+
						"\n"+
						getText(R.string.str15)+
						String.valueOf(table.totaltime)+
						getText(R.string.str16));				

				findViewById(R.id.linearLayout_main).setVisibility(8);
				findViewById(R.id.linearLayout_next).setVisibility(0);

				mathItems = getMathItem(settings.amount, settings.range, settings.type);				
			}
		});

		Button btn0 = (Button)findViewById(R.id.button_0);
		btn0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 0);
				getNumClicked();
			}
		});

		Button btn1 = (Button)findViewById(R.id.button_1);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 1);
				getNumClicked();
			}
		});

		Button btn2 = (Button)findViewById(R.id.button_2);
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 2);
				getNumClicked();            
			}
		});

		Button btn3 = (Button)findViewById(R.id.button_3);
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 3);
				getNumClicked();           
			}
		});

		Button btn4 = (Button)findViewById(R.id.button_4);
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 4);
				getNumClicked();            
			}
		});

		Button btn5 = (Button)findViewById(R.id.button_5);
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 5);
				getNumClicked();            
			}
		});

		Button btn6 = (Button)findViewById(R.id.button_6);
		btn6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 6);
				getNumClicked();            
			}
		});

		Button btn7 = (Button)findViewById(R.id.button_7);
		btn7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 7);
				getNumClicked();            
			}
		});

		Button btn8 = (Button)findViewById(R.id.button_8);
		btn8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 8);
				getNumClicked();            
			}
		});

		Button btn9 = (Button)findViewById(R.id.button_9);
		btn9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNum_answer(num_click, 9);
				getNumClicked();            
			}
		});

		Button btn10 = (Button)findViewById(R.id.button_clearHistory);
		btn10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clearData();         
			}
		});

		Button btn11 = (Button)findViewById(R.id.button_wrongQuestion);
		btn11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ArrayList<ErrorTable> tables;
				String operT = new String();
				String resultStr = new String();
				int i = 0;

				tables = queryData2();
				for (ErrorTable tab: tables) {
					i++;
					operT = getOperTypeString(tab.operType);
					resultStr += (
							"["+String.valueOf(i)+"]"+
									String.valueOf(tab.firstNum)+
									operT+
									String.valueOf(tab.secondNum)+
									"="+
									String.valueOf(tab.errorAnswer)+
									getText(R.string.str17)+
									String.valueOf(tab.rightAnswer)+
									"\n"
							);                    
				}

				((TextView)findViewById(R.id.textView_wrongQuestions)).setMovementMethod(ScrollingMovementMethod.getInstance());
				((TextView)findViewById(R.id.textView_wrongQuestions)).setText(resultStr);
			}
		});

		btn_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tv5.setText("");
				num_click = 0;
				num_answer = 0;
			}
		});

		btn_answer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btn_answer.setText(R.string.str27);
				Date start_Time = new Date();

				if ( false == num_started )
				{
					initTextView();
					startTime = start_Time.getTime();                
					tv6.setText( getText(R.string.str18)+
							String.valueOf(count+1)+
							"\n"+
							getText(R.string.str19)+
							String.valueOf(settings.amount));
					num_started = true;
				}
				else 
				{
					if (num_answer!=mathItems.answer[count])
					{
						if (false == settings.mode)
						{
							btn_answer.setBackgroundColor(Color.RED);
							player1.start();  							
						}
						else
						{
							recordData2(mathItems.firstnum[count], 
									mathItems.secondNum[count], 
									mathItems.OperType[count],
									num_answer,
									mathItems.answer[count]);

							num_error++;
							num_click = 0;
							num_answer = 0;
							if (count < (settings.amount-1))
							{
								count++;
								initTextView();
							}
							else
							{
								clearTextView();
								openResultDialog();
							}                        	
						}

						if (settings.amount >= count)
						{
							tv6.setText( getText(R.string.str18)+
									String.valueOf(count+1)+
									"\n"+
									getText(R.string.str19)+
									String.valueOf(settings.amount));
						}

						tv5.setText("");
						num_click = 0;
						num_answer = 0;
					}
					else
					{
						if (false == settings.mode)
						{
							btn_answer.setBackgroundColor(Color.GREEN);
							player2.start();  							
						}
						num_click = 0;
						num_answer = 0;
						if (count < (settings.amount-1))
						{
							count++;
							initTextView();
						}
						else
						{
							clearTextView();
							openResultDialog();
						}

						if (settings.amount >= count)
						{
							tv6.setText( getText(R.string.str18)+
									String.valueOf(count+1)+
									"\n"+
									getText(R.string.str19)+
									String.valueOf(settings.amount));
						}

					}
				} 
			}

		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.detector.onTouchEvent(event); //touch事件交给手势处理。
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1.getX()-e2.getX()>120){
			viewFlipper.setInAnimation(leftInAnimation);
			viewFlipper.setOutAnimation(leftOutAnimation);
			viewFlipper.showNext();//向右滑动
			return true;
		}else if(e1.getX()-e2.getY()<-120){
			viewFlipper.setInAnimation(rightInAnimation);
			viewFlipper.setOutAnimation(rightOutAnimation);
			viewFlipper.showPrevious();//向左滑动
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initTextView()
	{
		((TextView)(findViewById(R.id.textView_firstNum))).setText(String.valueOf(mathItems.firstnum[count]));
		((TextView)(findViewById(R.id.textView_secondNum))).setText(String.valueOf(mathItems.secondNum[count]));
		((TextView)(findViewById(R.id.textView_equal))).setText(" = ");
		((TextView)(findViewById(R.id.textView_answer))).setText("");
		setOperType();    	
	}

	public void clearTextView()
	{
		((TextView)(findViewById(R.id.textView_firstNum))).setText("");
		((TextView)(findViewById(R.id.textView_operator))).setText("");
		((TextView)(findViewById(R.id.textView_secondNum))).setText("");
		((TextView)(findViewById(R.id.textView_equal))).setText("");
		((TextView)(findViewById(R.id.textView_answer))).setText("");
		((TextView)(findViewById(R.id.textView_status))).setText("");
	}

	public int getOperType(int operT)
	{
		int oper = 0;

		switch(operT)
		{
		case 0:
			oper = R.string.str_plus;
			break;
		case 1:
			oper = R.string.str_minus;
			break;
		case 2:
			oper = R.string.str_multi;
			break;
		case 3:
			oper = R.string.str_devide;
			break;
		default:
			break; 	
		}

		return oper;
	}

	public String getOperTypeString(int operT)
	{
		String str = new String();

		switch(operT)
		{
		case 0:
			str = (String)getText(R.string.str_plus);
			break;
		case 1:
			str = (String)getText(R.string.str_minus);
			break;
		case 2:
			str = (String)getText(R.string.str_multi);
			break;
		case 3:
			str = (String)getText(R.string.str_devide);
			break;
		default:
			break;
		}

		return str;
	}


	public void setOperType()
	{
		int operT;

		operT = getOperType(mathItems.OperType[count]);		
		((TextView)(findViewById(R.id.textView_operator))).setText(operT);

	}

	public void getNumClicked()
	{
		((TextView)(findViewById(R.id.textView_answer))).setText(String.valueOf(num_answer));
		num_click++;
		if (num_click>5)
		{
			num_answer = 0;
			num_click = 0;
			((TextView)(findViewById(R.id.textView_answer))).setText("");
		}

	}
	public MathItem getMathItem(int count, int range, int type)
	{
		MathItem items = new MathItem();
		Random random = new Random();
		int num = 0;

		for (int i=0; i<count; i++)
		{

			if (2 == type)
			{
				items.OperType[i] = type;
				items.firstnum[i] = Math.abs(random.nextInt()%range);
				items.secondNum[i] = Math.abs(random.nextInt()%range);
			}
			else if ((1 == type)||(0==type))
			{
				items.OperType[i] = Math.abs(random.nextInt()%2);
				items.firstnum[i] = Math.abs(random.nextInt()%range);
				items.secondNum[i] = Math.abs(random.nextInt()%range);
				if (1==items.OperType[i])
				{
					if (items.firstnum[i]<items.secondNum[i])
					{
						num = items.firstnum[i];
						items.firstnum[i] = items.secondNum[i];
						items.secondNum[i] = num;
					}            		
				}
			}
			else if (3==type)
			{
				items.OperType[i] = type;
				items.secondNum[i] = Math.abs(random.nextInt()%range);
				if (0==items.secondNum[i])
				{
					items.secondNum[i] = 1;
				}
				items.firstnum[i] = Math.abs(random.nextInt()%range)*items.secondNum[i];
			}
			else if (4==type)
			{
				items.OperType[i] = Math.abs(random.nextInt()%type);
				items.firstnum[i] = Math.abs(random.nextInt()%range);
				items.secondNum[i] = Math.abs(random.nextInt()%range);
				if (3 == items.OperType[i])
				{
					if (0==items.secondNum[i])
					{
						items.secondNum[i] = 1;
					}
					items.firstnum[i] = Math.abs(random.nextInt()%range)*items.secondNum[i];
				} 
				else if (1==items.OperType[i])
				{
					if (items.firstnum[i]<items.secondNum[i])
					{
						num = items.firstnum[i];
						items.firstnum[i] = items.secondNum[i];
						items.secondNum[i] = num;
					}            		
				}

			}
			items.answer[i] = calculate(items.firstnum[i], items.secondNum[i], items.OperType[i] );
		}

		return items;
	}

	public int calculate(int num1, int num2, int type)
	{
		int answer = 0;

		switch (type)
		{
		case 0:
			answer = num1+num2;
			break;
		case 1:
			answer = num1-num2;
			break;
		case 2:
			answer = num1*num2;
			break;
		case 3:
			answer = num1/num2;
			break;
		default:
			break;
		}

		return answer;
	}

	public void getNum_answer(int num_click, int number)
	{
		switch (num_click)
		{
		case 0:
			num_answer = number;
			break;
		default:
			num_answer = num_answer*10+number;
			break;
		}
	}

	public MathTable queryData()
	{
		// Fetch a record from the database.
		String[] projection = {
				"_id","total","correct","totaltime"
		};
		Cursor cursor = getContentResolver().query(MyProvider.ADDRESS_CONTENT_URI, projection,
				null, null, null);
		MathTable table = new MathTable();

		if (cursor != null) {
			if (cursor.moveToNext() )
			{
				table.idx = cursor.getInt(0);
				table.total = cursor.getInt(1);
				table.correct = cursor.getInt(2);
				table.totaltime = cursor.getInt(3);
				table.firstRecord = false;
			}
			else
			{
				table.total = 0;
				table.correct = 0;
				table.totaltime = 0;
				table.firstRecord = true;
			}
		}

		cursor.close();		

		return table;
	}

	public ArrayList<ErrorTable> queryData2()
	{
		ArrayList<ErrorTable> tables = new ArrayList<ErrorTable>();

		// Fetch a record from the database.
		String[] projection = {
				"_id","firstNum","secondNum","operType","errorAnswer","rightAnswer"
		};
		Cursor cursor = getContentResolver().query(MyProvider.ADDRESS_CONTENT_URI2, projection,
				null, null, null);

		if (cursor != null) {
			while(cursor.moveToNext())
			{
				ErrorTable table = new ErrorTable();

				table.id = cursor.getInt(0);
				table.firstNum = cursor.getInt(1);
				table.secondNum = cursor.getInt(2);
				table.operType = cursor.getInt(3);
				table.errorAnswer = cursor.getInt(4);
				table.rightAnswer = cursor.getInt(5);

				tables.add(table);
			}
		}

		cursor.close();		

		return tables;		
	}

	public void recordData(int totalNum, int correctNum, int totalTime)
	{
		MathTable table = new MathTable();

		table = queryData();
		table.total += totalNum;
		table.correct += correctNum;
		table.totaltime += totalTime;

		ContentValues values = new ContentValues();
		values.put("total", table.total);
		values.put("correct", table.correct);
		values.put("totaltime", table.totaltime);

		String selection = "_id=?";
		String[] selectionArgs = {
				Integer.toString(table.idx)};

		if (table.firstRecord)
		{
			getContentResolver().insert(MyProvider.ADDRESS_CONTENT_URI,values );
		}
		else
		{
			getContentResolver().update(MyProvider.ADDRESS_CONTENT_URI,values,selection,selectionArgs );
		}

		((TextView)findViewById(R.id.textView_historyStats)).setText(getText( R.string.str12)+
				String.valueOf(table.total)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				String.valueOf(table.correct)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				String.valueOf(table.totaltime)+
				getText(R.string.str16)); 
	}

	public void recordData2(int firstN, int secondN, int operT, int errorA, int rightA)
	{
		ContentValues values = new ContentValues();
		values.put("firstNum", firstN);
		values.put("secondNum", secondN);
		values.put("operType", operT);
		values.put("errorAnswer", errorA);
		values.put("rightAnswer", rightA);

		getContentResolver().insert(MyProvider.ADDRESS_CONTENT_URI2,values );
	}	

	public void clearData()
	{
		getContentResolver().delete(MyProvider.ADDRESS_CONTENT_URI, null, null);
		getContentResolver().delete(MyProvider.ADDRESS_CONTENT_URI2, null, null);

		((TextView)findViewById(R.id.textView_currentStats)).setText(getText( R.string.str12)+
				"0"+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				"0"+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				"0"+
				getText(R.string.str16));                                                                             
		((TextView)findViewById(R.id.textView_historyStats)).setText(getText( R.string.str12)+
				"0"+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				"0"+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				"0"+
				getText(R.string.str16)); 
		((TextView)findViewById(R.id.textView_wrongQuestions)).setText("");
	}

	private void openResultDialog()
	{
		Builder builder = new Builder(MyTab.this);
		long time_gap = 0;
		Date endTime = new Date();
		time_gap = endTime.getTime() - startTime;

		builder.setTitle(R.string.str29);
		builder.setIcon(R.drawable.ben);
		
		builder.setMessage( getText(R.string.str28)+
				String.valueOf(count+1)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				String.valueOf((count+1-num_error))+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				Long.valueOf(time_gap/1000)+
				getText(R.string.str16));
		((TextView)findViewById(R.id.textView_currentStats)).setText( getText(R.string.str28)+
				String.valueOf(count+1)+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str14)+
				String.valueOf((count+1-num_error))+
				getText(R.string.str13)+
				"\n"+
				getText(R.string.str15)+
				Long.valueOf(time_gap/1000)+
				getText(R.string.str16));
		recordData((count+1),(count+1-num_error),(int)(time_gap/1000));

		builder.setPositiveButton(R.string.str27, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				findViewById(R.id.linearLayout_main).setVisibility(0);
				findViewById(R.id.linearLayout_next).setVisibility(8);
				num_started = false;
				((Button)findViewById(R.id.button_answer)).setText(R.string.str02);
				clearTextView();
				num_click = 0;
				num_error = 0;
				count = 0;
				num_answer = 0;
				player3.start();  
				dialog.cancel(); 
			}
		}).create().show();
	}

	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		paramMenu.add(0, 0, 0, R.string.str26);
		return super.onCreateOptionsMenu(paramMenu);
	}

	private void openOptionDialog()
	{
		Builder builder = new Builder(MyTab.this);
		builder.setTitle(R.string.str24);
		builder.setMessage(R.string.str25);
		builder.setIcon(R.drawable.clarabel);
		builder.setPositiveButton(R.string.str27, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).create().show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		if (0 == item.getItemId())
		{
			openOptionDialog();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		if (4 == paramInt)
		{
			Builder builder = new Builder(MyTab.this);
			builder.setTitle(R.string.str20);
			builder.setMessage(R.string.str21);
			builder.setPositiveButton(R.string.str22, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					MyTab.this.finish();
				}

			}).setNegativeButton(R.string.str23, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog1, int which1)
				{

				}
			}).show();

			return true;
		}
		return super.onKeyDown(paramInt,paramKeyEvent);
	}
}