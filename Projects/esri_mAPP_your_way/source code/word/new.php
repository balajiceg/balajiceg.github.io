
<!DOCTYPE html>
<html>
<body>

<form method="POST">




<p>

Welcome to a website to tackle BIOWAR.<br>
<br><br>
Please take some time to fill the form below.<br>
<br><br>
Remember the public are going to view the information that you post and follow it.<br> 
<br><br>
So please be as accurate as possible.
</p>


<br><br>
Name of the Doctor:<br>
<input type="text" name="firstname"/>
<br>
Email ID:<br>
<input type="email" name="email"/>
<br>
Phone number:<br>
<input type="text" name="phone" />
<br>
Name of the Communicable Disease:<br>
<input type="text" name="phone" />
<br>
<br>

Approximate date on which the disease is reported
  <input type="date" name="bday">
  <br>
<br>
Approximate time at which the disease is reported<br>
<input type="time" name="time" />
<br>
<br>
Postal Code of the Hospital Reporting:<br>
<input type="text" name="postalcode"  />
<br>
<br>

Age (Select one) of citizens most suseptible to thhis disease:<br>    
<input type="radio" name="age" ><18
<br><br>
<input type="radio" name="age" >18-21
<br><br>
<input type="radio" name="age" >22-25
<br><br>
<input type="radio" name="age" >26-35
<br><br>
<input type="radio" name="age" >36-45
<br><br>
<input type="radio" name="age" >45-55
<br><br>
<input type="radio" name="age" >55+
<br><br>
<br>


If this disease is capable of being spread to only one gender. Please Choose Gender (Select one) :<br>
<input type="radio" name="sex" value="male">Male
<br><br>
<input type="radio" name="sex" value="female">Female
<br><br>
<input type="radio" name="sex" value="Intersex">Intersex
<br><br>

<!--
In general would you say that your health is (Select one):<br>
<input type="radio" name="general_health" >Excellent
<br><br>
<input type="radio" name="general_health" >Very good
<br><br>
<input type="radio" name="general_health" >Good
<br><br>
<input type="radio" name="general_health" >Fair
<br><br>
<input type="radio" name="general_health" >Poor
<br><br>
-->
<!--
Have you been diagnosed with anxiety,depression or any other mental health disorder(Select one):<br>
<input type="radio" name="health" >yes
<br><br>
<input type="radio" name="health" >no
<br><br>
<input type="radio" name="health" >Prefer not to answer
<br><br>
-->
<!--
Do you use Twitter for professional or personel purposes(Select one):<br>
<input type="radio" name="twitter" >Personel
<br><br>
<input type="radio" name="twitter" >Professional
<br><br>

-->



Location of hospital -Latitude <br> (used only as a verification so provide approximate)
<input type="text" name="latitude">
<br>
<br><br>


Location of hospital- Longitude <br> (used only as a verification so provide approximate)
<input type="text" name="longitude" >
<br><br>
GIVE THE SYMPTOMS OF THE DISEASE:<br />
  <textarea name='symptoms' id='comment'></textarea><br />
 <br><br> 
GIVE SOME SIMPLE MEASURES THAT THE PUBLC CAN TAKE TO TACKLE THE DISEASE:<br />
  <textarea name='symptoms' id='comment'></textarea><br />
  
  <br><br>
  
<input type="submit" name="SUBMIT" value="SUBMIT">

<br><br>

<button onclick="myFunction()"> OK </button>

<br><br>
<script>
function myFunction() {
    alert("FORM SUCCESSFULLY SUBMITTED");
}
</script>

<br><br>
</form>
</body>
</html>

<?php


 if(isset($_POST['select']))
 {
$servername = "localhost";
$username = "cl47-a-wordp-44d";
$password = "UHVBr-wVn";
$dbname = "cl47-a-wordp-44d";

$firstname=$_POST['firstname'];
$email=$_POST['email'];
$phone=$_POST['phone'];
$time=$_POST['time'];
$postalcode=$_POST['postalcode'];
$age=$_POST['age'];
$sex=$_POST['sex'];
$generalhealth=$_POST['general_health'];
$health=$_POST['health'];
$twitter=$_POST['twitter'];
$lat=$_POST['latitude'];
$long=$_POST['longitude'];



// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "INSERT INTO `TABLE1` Values ('11','$firstname','$email','$phone','$postalcode','$age','$sex','$generalhealth','$health','$twitter','$lat','$long');";

if ($conn->query($sql) == TRUE) {
    echo "New record created successfully";
} 
else 
{
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
 }

?>
