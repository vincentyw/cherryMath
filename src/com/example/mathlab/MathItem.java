package com.example.mathlab;

class MathItem
{
    int[] firstnum;
    int[] secondNum;
    int[] OperType;
    int[] answer;

    public MathItem()
    {
        firstnum = new int[100];
        secondNum = new int[100];
        OperType = new int[100];
        answer = new int[100];
    }
}

class MathTable
{
	int idx;
	int total;
	int correct;
	int totaltime;
	boolean firstRecord;
	
	public MathTable()
	{
		idx = 1;
		total = 0;
		correct = 0;
		totaltime = 0;
		firstRecord = false;
	}
}

class ErrorTable
{
	int id;
	int firstNum;
	int secondNum;
	int operType;
	int errorAnswer;
	int rightAnswer;
	
	public ErrorTable()
	{
		id = 1;
		firstNum = 0;
		secondNum = 0;
		operType = 0;
		errorAnswer = 0;
		rightAnswer = 0;
	}
}