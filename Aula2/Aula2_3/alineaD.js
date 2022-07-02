alineaD = function () {

    var phones = db.phones.find({}, {_id:0, display:1}).toArray();

    for (var i = 0; i < phones.length; i++) {
        var display = Array.from(phones[i].display)
        var number_reversed = []
        var number = []
        var count = 20
        for (var j = 0; j < display.length; j++) {
            if (display[j] === "-") {
                count = j
            }
            if (count < j) {
                number += display[j]
            }
        }
        for (var h = number.length-1; h > -1; h--)
            number_reversed += number[h]
        if (number === number_reversed)
            print("Capicua: " + phones[i].display)
    }
}