
import sys
import string


def normalize_data(content):
	result = ''
	digit_len = 0

	for x in content:
		if x in string.digits:
			digit_len += 1
		else:
			if digit_len > 0:
				result += '<+'+str(digit_len)+'d>'
				digit_len = 0

		if x in string.letters:
			result += x
		if x == ' ' or x == '\n':
			result += x
	
	if digit_len > 0:
		result += '<+' + str(digit_len) + 'd>'

	return result	


if __name__ == '__main__':
	for line in sys.stdin:
		line = line.rstrip("\n")
		print normalize_data(line)	
