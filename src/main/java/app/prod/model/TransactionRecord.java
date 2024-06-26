package app.prod.model;

//Može raditi i sa stringovima i sa enumovima, kao i sa bilo kojim brojem
public record TransactionRecord<T, A extends Number>(T type, A amount) {
}
