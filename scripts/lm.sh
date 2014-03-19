

files=`ls ./corpus`

mkdir -p ./arpa/
mkdir -p ./bin_model/

for file in $files
do
	java -ea -mx2000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.MakeKneserNeyArpaFromText 3 ./arpa/${file}.arpa ./corpus/${file}
      
	java -ea -mx2000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.MakeLmBinaryFromArpa ./arpa/${file}.arpa ./bin_model/${file}

done

#java -ea -mx1000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.MakeKneserNeyArpaFromText 5 ./arpa/sys_weather.arpa ./corpus/sys_weather

#create bin model
#java -ea -mx1000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.MakeLmBinaryFromArpa ./arpa/sys_weather.arpa ./bin_model/sys_weather

#echo "This is a sample sentence ." | java -ea -mx1000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.ComputeLogProbabilityOfTextStream  ./bin_model/sys_weather
