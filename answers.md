# Lab 8: Database-Backed CRUD Application

- **Name:** Owen Ezekiel A. Anchola
- **Student ID:** 2511003
- **GitHub Repo:** https://github.com/owenezekielanchola-ops/webdev2

## Task 4: Apply `@Transactional` and Observe Rollback

After posting the two pets to `/api/pets/batch`, we can see in the SQL log that Hibernate attempted to execute an insert statement into the `pet` table for Milo (`MC-2001`). It then tried to do the same for Rocky, but using the same microchip number, `MC-2001`. As `microchipNumber` is unique, MySQL will not allow this second insertion. This causes Spring to throw a `DataIntegrityViolationException`. The `batchRegister` method is annotated with `@Transactional`, so both of these inserts were done within the same transaction. As the second pet failed, Spring rolled back the first insertion, so Milo wasn't added to the `pet` table.

## Task 5: Debug a Persistence Error

The stack trace showed that leaving out the `species` field caused a `PropertyReferenceException` (`No property 'species' found on type 'Pet'`) at startup. This is because the `PetRepository` uses `species` within its query methods. When trying to save using `petRepository.save(p)`, the stack trace throws a `JpaSystemException` (`IdentifierGenerationException: ids for this class must be generated or assigned manually before calling 'persist()'`) because there was no `@GeneratedValue` in the `id`. But then after adding `@GeneratedValue`, the data integrity violation error occurred because `p.setMicrochipNumber(null)` was still present. This error was a `DataIntegrityViolationException` (`not-null property references a null or transient value: com.example.tasktracker.model.Pet.microchipNumber`).

The SQL log didn't show any `'insert into pet'` queries. This is because Hibernate had already picked up on the missing ID generator and the null microchip value. After fixing these issues, the SQL log did show an `'insert into pet (breed,microchip_number,name,species) values (?,?,?,?)'`.

The solution involved adding `@GeneratedValue(strategy = GenerationType.IDENTITY)` underneath `@Id` so that MySQL would generate the primary key automatically. Adding the missing `species` and `breed` fields (with the getters and setters). And making sure that the `microchipNumber` was given a valid non-null value before `petRepository.save(p)`.
