int isdigit(char ch)
{
	return ch>='0'&&ch<='9';
}

void put_s() {

}

void put_i() {

}

void get_c() {

}

void main()
{
    char c;
	put_s("c=");
	c=get_c();
	put_i(isdigit(c));
}
