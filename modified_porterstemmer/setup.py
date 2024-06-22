import setuptools

with open("README.md", "r", encoding = "utf-8") as fh:
    long_description = fh.read()

setuptools.setup(
    name = "modified_porterstemmer",
    version = "0.0.1",
    author = "Abhinav Kumar",
    author_email = "anu55abhi@gmail.com",
    description = "A modified Porter stemmer for verbs and other additional rules.",
    long_description = long_description,
    long_description_content_type = "text/markdown",
    url = "https://github.com/abhinav16aero/PorterStemmerModified",
    project_urls = {
        "Bug Tracker": "https://github.com/abhinav16aero/PorterStemmerModified/issues",
    },
    classifiers = [
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    package_dir = {"": "src"},
    packages = setuptools.find_packages(where="src"),
    python_requires = ">=3.6"
)