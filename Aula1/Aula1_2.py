file = open("names.txt")
file2 = open("names_counting.txt", "w")

dic = {}

for string in file:
    string = string.lower().replace("\n", "")
    if string[0:1] not in dic.keys():
        dic[string[0:1]] = [string]
    else:
        dic[string[0:1]].append(string)

for key in dic.keys():
    file2.write("SET {} {}\n".format(str(key).upper(), len(dic.get(key))))