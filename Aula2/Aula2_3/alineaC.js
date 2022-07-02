alineaC = function () {

    var phones = db.phones.find({}, {_id:0, "components.prefix":1}).toArray();

    var prefixes = [21, 22, 231, 232, 233, 234];

    var count = [0, 0, 0, 0, 0, 0]

    for (var i = 0; i < phones.length; i++) {
        for (var j = 0; j < prefixes.length; j++) {
            var prefix = prefixes[j]
            if (phones[i].components.prefix === prefix) {
                count[j] = count[j] + 1
            }
        }
    }
   for (var i = 0; i < count.length; i++) 
    print("Prefix: " + prefixes[i] + "; Count: " + count[i])
}