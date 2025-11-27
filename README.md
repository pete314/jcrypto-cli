# jCrypto - CLI Encryption Tool

jCrypto is a command-line interface (CLI) tool for secure file encryption and decryption written in Java. It utilizes strong cryptographic standards, including AES encryption with PBKDF2 key derivation and Elliptic Curve Cryptography (ECIES) for secure key management.

## Features

*   **AES Encryption**: Uses AES/CBC/PKCS5Padding for data encryption.
*   **Strong Key Derivation**: Uses PBKDF2WithHmacSHA512 to generate secret keys from random salts and base keys.
*   **Elliptic Curve Cryptography**: Protects the encryption keys using ECIES (secp521r1 curve).
*   **File Integrity**: Includes CRC32 checksums to verify data integrity.
*   **Secure Randomness**: Utilizes `SHA1PRNG` for generating secure random numbers and IVs.
*   **Cloud Storage Support**: Basic support for configuring and using Amazon S3 for storage (work in progress).

## Prerequisites

*   **Java 8**: The project is built using Java 8.
*   **Maven**: Used for dependency management and building the project.

## Installation and Build

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd jcrypto-cli
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```
    This command will compile the code, run tests, and package the application into a JAR file. Dependencies will be copied to `target/lib`.

## Usage

The main entry point is `ie.peternagy.jcrypto.cli.JCryptoCli`. You can run the built JAR file as follows:

```bash
java -jar target/jcrypto-cli-1.0-SNAPSHOT.jar [options]
```

### Command Line Options

| Option | Long Option | Description |
| :--- | :--- | :--- |
| `-b` | `--benchmark` | Run a benchmark test on the implementation to measure throughput. |
| `-c` | `--configure` | Run the configuration utility to setup backup options (e.g., AWS S3). |
| `-f` | `--file` | The path to the file to encrypt or decrypt. |
| `-h` | `--help` | Show help message and examples. |
| `-v` | `--verbose` | Enable verbose output to show process details. |

### Examples

**1. Encrypt a file:**

To encrypt a file named `mysecret.txt`:

```bash
java -jar target/jcrypto-cli-1.0-SNAPSHOT.jar -f mysecret.txt
```
This will create an encrypted file named `mysecret.txt.enc`.

**2. Decrypt a file:**

To decrypt `mysecret.txt.enc`:

```bash
java -jar target/jcrypto-cli-1.0-SNAPSHOT.jar -f mysecret.txt.enc
```
This will restore the original file `mysecret.txt`.

**3. Run Benchmark:**

To test the encryption speed:

```bash
java -jar target/jcrypto-cli-1.0-SNAPSHOT.jar -b -v
```

**4. Configure Storage:**

To configure Amazon S3:

```bash
java -jar target/jcrypto-cli-1.0-SNAPSHOT.jar -c
```
Follow the on-screen prompts to enter your AWS credentials and bucket name.

## Project Structure

*   `ie.peternagy.jcrypto.algo`: Contains cryptographic algorithm wrappers (AES, Elliptic Curve).
*   `ie.peternagy.jcrypto.cli`: Contains the CLI entry point and argument parsing logic.
*   `ie.peternagy.jcrypto.module`: Contains modules for configuration, file handling, and storage.
*   `ie.peternagy.jcrypto.util`: Contains utility classes for file access, security, and signatures.

## Security Details

*   **Key Storage**: Elliptic Curve keys are generated and stored in the user's home directory under `.jcrypto/`.
*   **Encryption Header**: Encrypted files include a header containing the version, IV, salt, and the encrypted base key needed for decryption.
*   **Integrity Check**: The header includes a CRC32 checksum of the encrypted content to prevent tampering.

## License

This project is licensed under the GNU General Public License v3.0.
