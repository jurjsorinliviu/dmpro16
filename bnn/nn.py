import cPickle
import gzip
import numpy as np

def softmax(x):
    """Compute softmax values for each sets of scores in x."""
    return np.exp(x) / np.sum(np.exp(x), axis=0)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

# load ANN data
nn_data = np.load("2-layer.npz")
# Make synapses binary
nn_first_synapses = np.sign(nn_data["arr_0"]).astype(int)
print(nn_first_synapses)
nn_second_synapses = np.sign(nn_data["arr_6"]).astype(int)
print(nn_second_synapses)
nn_out_synapses = np.sign(nn_data["arr_12"]).astype(int)
print(nn_out_synapses)

# load image
# original dataset is 0-255, this one uses floats
with gzip.open('mnist.pkl.gz', 'rb') as f:
  _, _, test_set = cPickle.load(f)
# test_set[0] is the image vector
# test_set[1] is the expected output
# test_set contains 10000 images
total_images = 10000
correct_images = 0
for test_image in range(total_images):
    image_vector = test_set[0][test_image]
    for i in range(784):
        if image_vector[i] > 0.6: # colors are not linear
            image_vector[i] = 1
        else:
            image_vector[i] = -1
    
    # compute first hidden layer (1024)
    l1_size = 1024
    
    first_layer_in = np.dot(image_vector, nn_first_synapses)
    
    beta = nn_data["arr_2"]
    gamma = nn_data["arr_3"]
    mean = nn_data["arr_4"]
    inv_stddev = nn_data["arr_5"]
    
    x_with_mean = first_layer_in - mean
    first_layer_output = (gamma * x_with_mean * inv_stddev) + beta
    #first_layer_output = np.tanh(first_layer_output)
    first_layer_output = np.sign(first_layer_output).astype(int)
    
    # compute second hidden layer (128)
    l2_size = 128
    input_second_layer = np.dot(first_layer_output, nn_second_synapses)
    
    beta = nn_data["arr_8"]
    gamma = nn_data["arr_9"]
    mean = nn_data["arr_10"]
    inv_stddev = nn_data["arr_11"]
    
    x_with_mean = input_second_layer - mean
    second_layer_output = (gamma * x_with_mean * inv_stddev) + beta
    second_layer_output = np.sign(second_layer_output).astype(int)
    #second_layer_output = np.tanh(second_layer_output)

    # compute output layer (10)
    out_size = 10
    input_out_layer = np.dot(second_layer_output, nn_out_synapses)
    
    beta = nn_data["arr_14"]
    gamma = nn_data["arr_15"]
    mean = nn_data["arr_16"]
    inv_stddev = nn_data["arr_17"]

    x_with_mean = input_out_layer - mean
    output_layer_output = np.sign((gamma * x_with_mean * inv_stddev) + beta).astype(int)
    
    # update results
    output_from_nn = np.argmax(output_layer_output)
    correct_output = test_set[1][test_image]
    if output_from_nn == correct_output:
        correct_images += 1
        print("{}/{} correct images".format(correct_images, test_image+1))
